package maquina;

import conexao.Conexao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class Registro {
    Conexao conectar = new Conexao();
    JdbcTemplate conec = conectar.getConexao();
    Maquina maquina = new Maquina();
    Date data = new Date();
    private Timer permanenciaDeDados = new Timer();
    private Double consumoCPU;
    private Double consumoRam;
    private Double consumoDisco;
    private Integer totalDispositivos;


    private Double getConsumoRam() {
        return maquina.consumoRam();
    }

    private Double getConsumoCPU() {
        return maquina.consumoProcessador();
    }

    private Double getConsumoDisco() {
        return maquina.consumoTodosDiscos();
    }

    private Integer getTotalDispositivos() {
        return maquina.totalDispositvos();
    }

    public void inserirRegistros(Integer fkUsuario) {

        try {
            String query = "SELECT idMaquina FROM maquina WHERE fkUsuario = ?";
            conec.queryForObject(query, new Object[]{fkUsuario}, (resposta, indice) -> {
                Integer idMaquina = resposta.getInt(1);

                permanenciaDeDados.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        conec.update("""
                                INSERT INTO registro(dataHora, fkMaquina, consumoCpu, consumoRam, consumoDisco, totalDispositivosConectados) VALUES (?, ?, ?, ?, ?, ?)
                                """, new Timestamp(data.getTime()), idMaquina, getConsumoCPU(),getConsumoRam(), getConsumoDisco(), getTotalDispositivos()
                                );
                    }
                },10000, 2000);
                return null;
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Não foi encontrada nenhuma máquina vinculada a este usuário");
        }
    }
}
