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

import java.util.List;
import java.util.Scanner;

public class Maquina {
    ConexaoMySQL conectar = new ConexaoMySQL();
    JdbcTemplate conMySQL = conectar.getConexao();

    Looca looca = new Looca();

    ConexaoSQL conexaoSQL = new ConexaoSQL();
    JdbcTemplate conSQL = conexaoSQL.getConexaosql();

    private Integer idMaquina;
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

    private void cadastrarMaquina(Integer idUsuario) {
        Integer idMaquina = conSQL.queryForObject("SELECT TOP 1 idMaquina FROM maquina WHERE fkUsuario = ? AND hostname IS NULL",Integer.class, idUsuario);

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
                        """, idUsuario, hostname);
            } catch (DataAccessException e) {
                // Erro caso não insira os dados no banco
                Logger.escreverLogExceptions("Ocorreu um erro de inserção na linha %s da Classe %s: %s"
                        .formatted(Logger.getNumeroDaLinha(), Logger.getNomeDaClasse(e), e));
            }

            try {
                // Atualizando atributos de recurso da tabela Maquina
                conMySQL.update("UPDATE maquina SET ip = ? ,hostname = ?, sistemaOperacional = ?, maxCpu = ?,maxRam = ?,maxDisco = ? WHERE idMaquina = ?",
                        getIp(), getHostname(), getSistemaOperacional(), getMaxCpu(), getMaxRam(), getMaxDisco(), idMaquina);

                // Adicionando limitador referente a tabela Maquina do usuario
                conMySQL.update("""
                        INSERT INTO limitador (fkMaquina, limiteCpu, limiteRam, limiteDisco)
                                            SELECT
                                                idMaquina,
                                                maxCpu * 0.8,  -- Reduzindo o limite de CPU em 20%
                                                maxRam * 0.8,  -- Reduzindo o limite de RAM em 20%
                                                maxDisco * 0.8  -- Reduzindo o limite de disco em 20%
                                            FROM maquina where fkUsuario = ? AND hostname = ?
                        """, idUsuario, hostname);
            } catch (DataAccessException e) {
                // Erro caso não insira os dados no banco
                Logger.escreverLogExceptions("Ocorreu um erro de inserção na linha %s da Classe %s: %s"
                        .formatted(Logger.getNumeroDaLinha(), Logger.getNomeDaClasse(e), e));
            }
            verificarMaquina(idUsuario);
    }

    public void verificarMaquina(Integer idUsuario) {
        try {
            // Procura maquina no banco pelo HOSTNAME
            Integer idMaquina = conSQL.queryForObject("SELECT idMaquina FROM maquina WHERE fkUsuario = ? AND hostname = ?"
                    , Integer.class, idUsuario, this.hostname);

            if (idMaquina != null) { // Caso exista o hostname no banco
                this.idMaquina = idMaquina;

                System.out.println("""
                    *------------------------------------*
                    |              Sistema               |
                    *------------------------------------*
                    |Dispositivo Identificado!           |
                    *------------------------------------*
                        """);

                Limite limite = new Limite(idMaquina);
                while (true) {
                    Registro registro = new Registro();
                    registro.inserirRegistros(idMaquina, limite);
                }
            }
        } catch (EmptyResultDataAccessException e) {
            // Caso não exista esse HOSTNAME no banco
            System.out.println("""
                    *------------------------------------*
                    |              Sistema               |
                    *------------------------------------*
                    |O dispositivo que você está usando  |
                    |não está cadastrado!                |
                    |Indo para o cadastro do dispositivo.|
                    *------------------------------------*
                        """);
            cadastrarMaquina(idUsuario);
        }
    }
    //Getter

    public Integer getIdMaquina() {
        return idMaquina;
    }

    public String getNomeMaquina() {
        return nomeMaquina;
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

    public String getHostname() {
        return hostname;
    }

    public void setIdMaquina(Integer idMaquina) {
        this.idMaquina = idMaquina;
    }

    public void setNomeMaquina(String nomeMaquina) {
        this.nomeMaquina = nomeMaquina;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.sistemaOperacional = sistemaOperacional;
    }

    public void setMaxCpu(Double maxCpu) {
        this.maxCpu = maxCpu;
    }

    public void setMaxRam(Double maxRam) {
        this.maxRam = maxRam;
    }

    public void setMaxDisco(Double maxDisco) {
        this.maxDisco = maxDisco;
    }

    @Override
    public String toString() {
        return "Maquina{" +
                "idMaquina=" + idMaquina +
                ", nome='" + nomeMaquina + '\'' +
                ", hostname='" + hostname + '\'' +
                ", ip='" + ip + '\'' +
                ", sistemaOperacional='" + sistemaOperacional + '\'' +
                ", maxCpu=" + maxCpu +
                ", maxRam=" + maxRam +
                ", maxDisco=" + maxDisco +
                '}';
    }
}
