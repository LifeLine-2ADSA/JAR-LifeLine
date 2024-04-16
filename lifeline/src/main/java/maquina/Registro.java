package maquina;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import conexao.Conexao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Registro {
    Conexao conectar = new Conexao();
    JdbcTemplate conec = conectar.getConexao();
    Looca looca = new Looca();
    Date data = new Date();
    private Timer permanenciaDeDados;
    private Double consumoCPU;
    private Double consumoRam;
    private Double consumoDisco;
    private Integer totalDispositivos;

    //Construtor
    public Registro() {
        Double disco = 0.0;
        for (int i = 0; i < looca.getGrupoDeDiscos().getVolumes().size(); i++) {
            disco += Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getGrupoDeDiscos().getVolumes().get(i).getTotal() - looca.getGrupoDeDiscos().getVolumes().get(i).getDisponivel()));
        }
        this.permanenciaDeDados = new Timer();
        this.consumoCPU = Conversor.converterDoubleDoisDecimais(looca.getProcessador().getUso());
        this.consumoRam = Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getMemoria().getEmUso()));
        this.consumoDisco = disco;
        this.totalDispositivos = looca.getDispositivosUsbGrupo().getTotalDispositvosUsbConectados();
    }

    public void inserirRegistros(Integer fkUsuario, String macAddress) {

        try {
            String query = "SELECT idMaquina FROM maquina WHERE fkUsuario = ? AND macAddress = ?";
            conec.queryForObject(query, new Object[]{fkUsuario, macAddress}, (resposta, indice) -> {
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

    public Double getConsumoCPU() {
        return consumoCPU;
    }

    public Double getConsumoRam() {
        return consumoRam;
    }

    public Double getConsumoDisco() {
        return consumoDisco;
    }

    public Integer getTotalDispositivos() {
        return totalDispositivos;
    }

    @Override
    public String toString() {
        return """
                Registro
                Consumo de CPU: %.2f
                Consumo de RAM: %.2f
                Consumo de Disco: %.2f
                Total de Dispositivos: %d
                """.formatted(consumoCPU, consumoRam, consumoDisco, totalDispositivos);
    }
}
