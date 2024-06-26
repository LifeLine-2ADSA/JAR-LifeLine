package maquina;

import service.ConexaoMySQL;
import service.ConexaoSQL;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Limite {
    ConexaoMySQL conectar = new ConexaoMySQL();
    JdbcTemplate conMySQL = conectar.getConexao();
    ConexaoSQL conexaoSQL = new ConexaoSQL();
    JdbcTemplate conSQL = conexaoSQL.getConexao();
    private Double limiteCPU;
    private Double limiteRam;
    private Double limiteDisco;

    public Limite(Integer idMaquina) {
        try {
            String sql = "SELECT TOP 1 limiteCpu, limiteRam,limiteDisco FROM limitador WHERE fkMaquina = ?";
            conSQL.queryForObject(sql, new Object[]{idMaquina}, (resposta, indice) -> {
                this.limiteCPU = resposta.getDouble(1);
                this.limiteRam = resposta.getDouble(2);
                this.limiteDisco = resposta.getDouble(3);
                return null;
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Limite da maquina não encontrado");
        }
    }

    public Double getLimiteCPU() {
        return limiteCPU;
    }

    public void setLimiteCPU(Double limiteCPU) {
        this.limiteCPU = limiteCPU;
    }

    public Double getLimiteRam() {
        return limiteRam;
    }

    public void setLimiteRam(Double limiteRam) {
        this.limiteRam = limiteRam;
    }

    public Double getLimiteDisco() {
        return limiteDisco;
    }

    public void setLimiteDisco(Double limiteDisco) {
        this.limiteDisco = limiteDisco;
    }
}
