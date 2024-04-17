package maquina;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import conexao.Conexao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Registro {
    Conexao conectar = new Conexao();
    JdbcTemplate conec = conectar.getConexao();
    Looca looca = new Looca();
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

    public void inserirRegistros(Integer idMaquina) {
        try {
                permanenciaDeDados.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Date data = new Date();
                        conec.update("""
                                INSERT INTO registro(dataHora, fkMaquina, consumoCpu, consumoRam, consumoDisco, consumoDispositivos) VALUES (?, ?, ?, ?, ?, ?)
                                """, new Timestamp(data.getTime()), idMaquina, getConsumoCPU(),getConsumoRam(), getConsumoDisco(), getTotalDispositivos()
                                );

                        System.out.println(new Timestamp(data.getTime()));
                        System.out.println("""
                    *------------------------------------*
                    |           Dados Coletados          |
                    *------------------------------------*
                    |Consumo da CPU: %.2f               |
                    |Consumo da RAM: %.2f                |
                    |Consumo da Disco: %.2f            |
                    |Quantidade de Dispositivos: %d       |
                    *------------------------------------*
                                """.formatted(getConsumoCPU(), getConsumoRam(),getConsumoDisco(),getTotalDispositivos()));
                    }
                },50000, 25000);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Não foi encontrada nenhuma máquina vinculada a este usuário");
        }
        triggerRegistro(idMaquina);
    }

    private void triggerRegistro(Integer idMaquina) {
        try {
            String sql = "SELECT limiteCpu, limiteRam,limiteDisco FROM limitador WHERE fkMaquina = ?";
            conec.queryForObject(sql, new Object[]{idMaquina}, (resposta, indice) -> {
                if (resposta.getDouble(1) > getConsumoCPU() ||
                        resposta.getDouble(2) > getConsumoRam()
                        //resposta.getDouble(3) > getConsumoDisco()
                ) {
                    Date data = new Date();
                    Integer fkRegistro = conec.queryForObject("SELECT idRegistro FROM registro ORDER BY idRegistro DESC LIMIT 1", Integer.class);

                    conec.update("INSERT INTO alerta(dataAlerta, fkRegistro) VALUES (?, ?)", new Timestamp(data.getTime()), fkRegistro);

                    System.out.println("""
                            ----------------------------------------------------------------------------------------------------------------
                            ALERTEI!!!!!!!!!!!
                            ----------------------------------------------------------------------------------------------------------------
                            """);
                }
                return null;
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e);
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
