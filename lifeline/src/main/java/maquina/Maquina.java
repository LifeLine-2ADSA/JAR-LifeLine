package maquina;

import Logs.Logger;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import conexao.ConexaoMySQL;
import conexao.ConexaoSQL;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import usuario.Usuario;

import java.util.List;
import java.util.Scanner;

public class Maquina {
    ConexaoMySQL conectar = new ConexaoMySQL();
    JdbcTemplate conMySQL = conectar.getConexao();

    Looca looca = new Looca();

    ConexaoSQL conexaoSQL = new ConexaoSQL();
    JdbcTemplate conSQL = conexaoSQL.getConexaosql();

    private Integer idMaquinaSQL;
    private Integer idMaquinaMySQL;
    private String nomeMaquina;
    private String hostname;
    private String ip;
    private String sistemaOperacional;
    private Double maxCpu;
    private Double maxRam;
    private Double maxDisco;

    public Maquina() {
        List<RedeInterface> listaRede = looca.getRede().getGrupoDeInterfaces().getInterfaces().stream().filter(redeInterface -> !redeInterface.getEnderecoIpv4().isEmpty()).toList();
        this.hostname = looca.getRede().getParametros().getHostName();
        this.ip = listaRede.get(0).getEnderecoIpv4().get(0);
        this.sistemaOperacional = looca.getSistema().getSistemaOperacional();
        this.maxCpu = Conversor.converterFrequencia(looca.getProcessador().getFrequencia());
        this.maxRam = Conversor.converterDoubleDoisDecimais(Conversor.formatarBytes(looca.getMemoria().getTotal()));
        this.maxDisco = Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getGrupoDeDiscos().getTamanhoTotal()));
    }

    private Integer pegarIdMaquinaSQL(Integer idUsuarioSQL) {
        try {
            // retornando id da maquina sql
            return conSQL.queryForObject("SELECT TOP 1 idMaquina FROM maquina WHERE fkUsuario = ? AND hostname = ?",Integer.class, idUsuarioSQL, this.hostname);
        } catch (DataAccessException d) {
            Integer idMaquina = conSQL.queryForObject("SELECT TOP 1 idMaquina FROM maquina WHERE fkUsuario = ? AND hostname IS NULL",Integer.class, idUsuarioSQL);
            return cadastrarMaquinaSQL(idUsuarioSQL, idMaquina); // associando maquina
        }
    }
    private Integer cadastrarMaquinaSQL(Integer idUsuarioSQL, Integer idMaquina) {
        try {
            // Atualizando atributos de recurso da tabela Maquina
            conSQL.update("UPDATE maquina SET ip = ? ,hostname = ?, sistemaOperacional = ?, maxCpu = ?,maxRam = ?,maxDisco = ? WHERE idMaquina = ?",
                    getIp(), getHostname(), getSistemaOperacional(), getMaxCpu(), getMaxRam(), getMaxDisco(), idMaquina);

            // Adicionando limitador referente a tabela Maquina do usuario
            conSQL.update("""
                        INSERT INTO limitador (fkMaquina, limiteCpu, limiteRam, limiteDisco)
                                            SELECT
                                                idMaquina,
                                                maxCpu * 0.8,  -- Reduzindo o limite de CPU em 20%
                                                maxRam * 0.8,  -- Reduzindo o limite de RAM em 20%
                                                maxDisco * 0.8  -- Reduzindo o limite de disco em 20%
                                            FROM maquina where fkUsuario = ? AND hostname = ?
                        """, idUsuarioSQL, hostname);

            // retornando id da maquina sql
            return conSQL.queryForObject("SELECT TOP 1 idMaquina FROM maquina WHERE fkUsuario = ? AND hostname IS NULL",Integer.class, idUsuarioSQL);
        } catch (DataAccessException e) {
            // Erro caso não insira os dados no banco
            Logger.escreverLogExceptions("Ocorreu um erro de inserção na linha %s da Classe %s: %s"
                    .formatted(Logger.getNumeroDaLinha(), Logger.getNomeDaClasse(e), e));
            return null;
        }
    }

    private Integer pegarIdMaquinaMySQL(Integer idUsuarioMySQL) {
        try {
            return conMySQL.queryForObject("SELECT idMaquina FROM maquina WHERE fkUsuario = ? AND hostname = ?"
                    , Integer.class, idUsuarioMySQL, this.hostname);
        } catch (DataAccessException e) {
            conMySQL.update("INSERT INTO maquina(nomeMaquina, hostname, ip, sistemaOperacional, maxCpu, maxRam, maxDisco, fkUsuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    getNomeMaquina(), getHostname(), getIp(), getSistemaOperacional(), getMaxCpu(), getMaxRam(), getMaxDisco(), idUsuarioMySQL);

            // Adicionando limitador referente a tabela Maquina do usuario
            conMySQL.update("""
                        INSERT INTO limitador (fkMaquina, limiteCpu, limiteRam, limiteDisco)
                                            SELECT
                                                idMaquina,
                                                maxCpu * 0.8,  -- Reduzindo o limite de CPU em 20%
                                                maxRam * 0.8,  -- Reduzindo o limite de RAM em 20%
                                                maxDisco * 0.8  -- Reduzindo o limite de disco em 20%
                                            FROM maquina where fkUsuario = ? AND hostname = ?
                        """, idUsuarioMySQL, hostname);

            return conMySQL.queryForObject("SELECT idMaquina FROM maquina WHERE fkUsuario = ? AND hostname = ?"
                    , Integer.class, idUsuarioMySQL, this.hostname);
        }
    }

    public void verificarMaquina(Integer idUsuarioSQL, Integer idUsuarioMySQL) {
        this.idMaquinaSQL = pegarIdMaquinaSQL(idUsuarioSQL);
        this.idMaquinaMySQL = pegarIdMaquinaMySQL(idUsuarioMySQL);

        if (idMaquinaSQL != null && idUsuarioMySQL != null) {

            System.out.println("""
                    *------------------------------------*
                    |              Sistema               |
                    *------------------------------------*
                    |Dispositivo Identificado!           |
                    *------------------------------------*
                        """);

            Limite limite = new Limite(idMaquinaSQL);
            while (true) {
                Registro registro = new Registro();
                registro.inserirRegistros(idMaquinaSQL, idMaquinaMySQL, limite);
            }
        }
    }

    public Integer getIdMaquinaSQL() {
        return idMaquinaSQL;
    }

    public Integer getIdMaquinaMySQL() {
        return idMaquinaMySQL;
    }

    public String getNomeMaquina() {
        return nomeMaquina;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIp() {
        return ip;
    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public Double getMaxCpu() {
        return maxCpu;
    }

    public Double getMaxRam() {
        return maxRam;
    }

    public Double getMaxDisco() {
        return maxDisco;
    }

    @Override
    public String toString() {
        return "Maquina{" +
                "idMaquinaSQL=" + idMaquinaSQL +
                ", idMaquinaMySQL=" + idMaquinaMySQL +
                ", nomeMaquina='" + nomeMaquina + '\'' +
                ", hostname='" + hostname + '\'' +
                ", ip='" + ip + '\'' +
                ", sistemaOperacional='" + sistemaOperacional + '\'' +
                ", maxCpu=" + maxCpu +
                ", maxRam=" + maxRam +
                ", maxDisco=" + maxDisco +
                '}';
    }
}
