package maquina;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import com.sun.tools.jconsole.JConsoleContext;
import conexao.Conexao;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Maquina {
    Conexao conectar = new Conexao();
    JdbcTemplate conec = conectar.getConexao();
    Looca looca = new Looca();

    private Integer idMaquina;
    private String nomeMaquina;
    private String hostname;
    private String ip;
    private String sistemaOperacional;
    private Double maxCpu;
    private Double maxRam;
    private Double maxDisco;

    private void coletarDadosMaquina() {
        List<RedeInterface> listaRede = looca.getRede().getGrupoDeInterfaces().getInterfaces().stream().filter(redeInterface -> !redeInterface.getEnderecoIpv4().isEmpty()).toList();

        this.hostname = looca.getRede().getParametros().getHostName();
        this.ip = listaRede.get(0).getEnderecoIpv4().get(0);
        this.sistemaOperacional = looca.getSistema().getSistemaOperacional();
        this.maxCpu = Conversor.converterFrequencia(looca.getProcessador().getFrequencia());
        this.maxRam = Conversor.converterDoubleDoisDecimais(Conversor.formatarBytes(looca.getMemoria().getTotal()));
        this.maxDisco = Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getGrupoDeDiscos().getTamanhoTotal()));
    }

    private Integer escolhaMaquinas(Integer idUsuario) {
        try {
            // Procura maquinas com MAC Address nula com nome no banco
            List<Maquina> listaMaquinas = conec.query("SELECT m.idMaquina, m.nomeMaquina, m.hostname, m.ip, m.sistemaOperacional, m.maxCpu, m.maxRam, m.maxDisco FROM maquina m WHERE fkUsuario = ? AND hostname = hostname IS NULL", new BeanPropertyRowMapper<>(Maquina.class), idUsuario);

            // Caso exista maquinas sem atributos recurso com nome
            if (!listaMaquinas.isEmpty()) {
                System.out.println("Escolha a máquina que deseja associar:");
                //Iterando lista
                for (int i = 0; i < listaMaquinas.size(); i++) {
                    System.out.println(i + 1 + " - " + listaMaquinas.get(i).getNomeMaquina());
                }
                // Usuario escolhe qual maquina associar
                Scanner leitor = new Scanner(System.in);
                int escolha = leitor.nextInt();
                // retorna o id da maquina escolhida pelo usuario
                if (escolha > 0 && escolha <= listaMaquinas.size()) {
                    return listaMaquinas.get(escolha - 1).getIdMaquina();
                }
            } else {
                System.out.println("""
                    *------------------------------------*
                    |              Sistema               |
                    *------------------------------------*
                    |Error: Cadastre uma máquina no site!|
                    *------------------------------------*
                        """);
            }
        } catch (EmptyResultDataAccessException e) {
            // Caso não exista maquinas sem atributos recurso com nome
            System.out.println("Sem máquinas para associar.");
        }
        return 0;
    }

    private void cadastrarMaquina(Integer idUsuario) {
        Integer idMaquina = escolhaMaquinas(idUsuario);
        if (idMaquina > 0) {
            try {
                coletarDadosMaquina();
                // Atualizando atributos de recurso da tabela Maquina
                conec.update("UPDATE maquina SET ip = ? ,hostname = ?, sistemaOperacional = ?, maxCpu = ?,maxRam = ?,maxDisco = ? WHERE idMaquina = ?", getIp(), getHostname(), getSistemaOperacional(), getMaxCpu(), getMaxRam(), getMaxDisco(), idMaquina);

                // Adicionando limitador referente a tabela Maquina do usuario
                conec.update("""
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
                System.out.println(e);
            }

            verificarMaquina(idUsuario);
        }
    }

    public void verificarMaquina(Integer idUsuario) {
        coletarDadosMaquina();
        try {
            // Procura maquina no banco pelo MAC Address
            Integer idMaquina = conec.queryForObject("SELECT idMaquina FROM maquina WHERE fkUsuario = ? AND hostname = ? LIMIT 1"
                    , Integer.class, idUsuario, hostname);

            if (idMaquina != null) { // Caso exista o MAC Address no banco
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
            // Caso não exista esse MAC Address no banco
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
