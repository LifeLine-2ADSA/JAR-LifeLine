package maquina;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import conexao.Conexao;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class Maquina {
    Conexao conectar = new Conexao();
    JdbcTemplate conec = conectar.getConexao();
    Looca looca = new Looca();
    private String macAddress;
    private String ip;
    private String sistemaOperacional;
    private Double maxCpu;
    private Double maxRam;
    private Double maxDisco;

    public Maquina() {
        List<RedeInterface> listaRede = looca.getRede().getGrupoDeInterfaces().getInterfaces().stream().filter(redeInterface -> !redeInterface.getEnderecoIpv4().isEmpty()).toList();

        this.macAddress = listaRede.get(0).getEnderecoMac();
        this.ip = listaRede.get(0).getEnderecoIpv4().get(0);
        this.sistemaOperacional = looca.getSistema().getSistemaOperacional();
        this.maxCpu = Conversor.converterFrequencia(looca.getProcessador().getFrequencia());
        this.maxRam = Conversor.converterDoubleDoisDecimais(Conversor.formatarBytes(looca.getMemoria().getTotal()));
        this.maxDisco = Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getGrupoDeDiscos().getTamanhoTotal()));
    }

    public void cadastrarMaquina(String nome, Integer idUsuario) {
        try {
            conec.update("INSERT INTO maquina (nome, ip, macAddress, sistemaOperacional, maxCpu, maxRam, maxDisco, fkUsuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    nome, getIp(), getMacAddress(), getSistemaOperacional(), getMaxCpu(), getMaxRam(), getMaxDisco(), idUsuario);
            conec.update("""
                    INSERT INTO limitador (fkMaquina, limiteCpu, limiteRam, limiteDisco)
                                        SELECT
                                            idMaquina,
                                            maxCpu * 0.8,  -- Reduzindo o limite de CPU em 20%
                                            maxRam * 0.8,  -- Reduzindo o limite de RAM em 20%
                                            maxDisco * 0.8  -- Reduzindo o limite de disco em 20%
                                        FROM maquina where fkUsuario = ? AND macAddress = ?
                    """, idUsuario, macAddress);
        } catch (DataAccessException e) {
            System.out.println(e);
        }
    }

    public boolean verificarMaquina(Integer idUsuario) {
        try {
            Maquina maquinaVerificacao = conec.queryForObject("SELECT * FROM maquina WHERE fkUsuario = ? AND macAddress = ? LIMIT 1", new BeanPropertyRowMapper<>(Maquina.class), idUsuario, macAddress);
            if (maquinaVerificacao != null) {
                System.out.println("""
                        Identificamos que o dispositivo que você está usando está cadastrado!
                        """);
                return true;
            }
        } catch (EmptyResultDataAccessException e) {
            System.out.println("""
                        Identificamos que o dispositivo que você está usando não está cadastrado!
                        """);
        }
        return false;
    }


    //Getter
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

    public String getMacAddress() {
        return macAddress;
    }

    @Override
    public String toString() {
        return "Maquina{" +
                "macAddress='" + macAddress + '\'' +
                ", ip='" + ip + '\'' +
                ", sistemaOperacional='" + sistemaOperacional + '\'' +
                ", maxCpu=" + maxCpu +
                ", maxRam=" + maxRam +
                ", maxDisco=" + maxDisco +
                '}';
    }
}
