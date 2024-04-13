package maquina;

import com.github.britooo.looca.api.core.Looca;
import conexao.Conexao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Maquina {
    Conexao conectar = new Conexao();
    JdbcTemplate con = conectar.getConexao();
    Looca looca = new Looca();

    private String ip;
    private String sistemaOperacional;
    private Double limiteCpu;
    private Double limiteRam;
    private Double limiteDisco;

    public Maquina() {
        this.ip = looca.getRede().getParametros().getServidoresDns().get(2);
        this.sistemaOperacional = looca.getSistema().getSistemaOperacional();
        this.limiteCpu = Conversor.converterFrequencia(looca.getProcessador().getFrequencia());
        this.limiteRam = Conversor.converterDoubleDoisDecimais(Conversor.formatarBytes(looca.getMemoria().getTotal()));
        this.limiteDisco = Conversor.converterDoubleDoisDecimais(Conversor.formatarBytes(looca.getGrupoDeDiscos().getTamanhoTotal()));
    }

    public void cadastrarMaquina(String email, String nome) {
        Maquina maquina = new Maquina();

        try {
            con.queryForObject("SELECT m.idMaquina FROM maquina m JOIN usuario u ON m.idMaquina = u.fkMaquina JOIN usuario u ON u.fkUsuario = u.idUsuario; WHERE usuario.email = ? & maquina.nome = ?",Integer.class, email, nome);

            System.out.println("Você já cadastrou as informações do hardware no sistema!");
        } catch(EmptyResultDataAccessException e) {
            con.update("INSERT INTO maquina (nome, ip, sistemaOperacional, limiteCpu, limiteRam, limiteDisco) VALUES (?, ?, ?, ?, ?)",
                    nome, maquina.getIp(), maquina.getSistemaOperacional(), maquina.getLimiteCpu(), maquina.getLimiteRam(), maquina.getLimiteDisco());
        }
    }

    //Getter
    public String getIp() {
        return ip;
    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public Double getLimiteCpu() {
        return limiteCpu;
    }

    public Double getLimiteRam() {
        return limiteRam;
    }

    public Double getLimiteDisco() {
        return limiteDisco;
    }
}
