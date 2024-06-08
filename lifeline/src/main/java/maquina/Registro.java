package maquina;

import com.github.britooo.looca.api.core.Looca;
import service.ConexaoMySQL;
import service.ConexaoSQL;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import service.Conversor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class Registro {
    ConexaoMySQL conectar = new ConexaoMySQL();
    JdbcTemplate conMySQL = conectar.getConexao();
    ConexaoSQL conexaoSQL = new ConexaoSQL();
    JdbcTemplate conSQL = conexaoSQL.getConexao();
    Looca looca = new Looca();
    private Timer permanenciaDeDados;
    private Double consumoCPU;
    private Double consumoRam;
    private Double consumoDisco;
    private Double temperatura;
    private String nomeJanela;

    //Construtor
    public Registro() {
        Double disco = 0.0;
        for (int i = 0; i < looca.getGrupoDeDiscos().getVolumes().size(); i++) {
            disco += Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getGrupoDeDiscos().getVolumes().get(i).getTotal() - looca.getGrupoDeDiscos().getVolumes().get(i).getDisponivel()));
        }

//        Processo processoMaisConsome = looca.getGrupoDeProcessos().getProcessos().get(0);
//        for (Processo processo : looca.getGrupoDeProcessos().getProcessos()) {
//            if (processoMaisConsome.getBytesUtilizados() < pocesso.getBytesUtilizados()) {
//                processoMaisConsome = processo;
//            }
//        }
//        Janela janelaProcesso = looca.getGrupoDeJanelas().getJanelas().get(0);
//        for (Janela janela : looca.getGrupoDeJanelas().getJanelas()) {
//            if (processoMaisConsome.getPid() == janela.getPid().intValue()) {
//                janelaProcesso = janela;
//            }
//        }

        try {
            this.nomeJanela = ProcessInfo.getTopProcessAndWindow();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falha ao obter informações do processo.");
            this.nomeJanela = "Janela não encontrada";
        }

        this.temperatura = looca.getTemperatura().getTemperatura().doubleValue();
        this.permanenciaDeDados = new Timer();

        Double maxCPU = Conversor.converterFrequencia(looca.getProcessador().getFrequencia());
        Double consumoCPU = Conversor.converterDoubleDoisDecimais(looca.getProcessador().getUso());
        this.consumoCPU = maxCPU <= consumoCPU ? maxCPU : consumoCPU;

        this.consumoRam = Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getMemoria().getEmUso()));
        this.consumoDisco = disco;
    }

    public void inserirRegistros(Integer idMaquinaSQL, Integer idMaquinaMySQL, Limite trigger) {

        try {
            // coletar o timer para permanencia de dados
//            Integer interval = conSQL.queryForObject("SELECT timer FROM maquina WHERE idMaquina = ?", Integer.class, idMaquina);
            Integer interval = 10 * 1000;

            permanenciaDeDados.schedule(new TimerTask() {
                @Override
                public void run() {
                    Date data = new Date(); // data da coleta
                    // insert ao banco
                    conMySQL.update("""
                        INSERT INTO registro(dataHora, fkMaquina, consumoCpu, consumoRam, consumoDisco, nomeJanela, temperatura) VALUES (?, ?, ?, ?, ?, ?, ?)
                        """, new Timestamp(data.getTime()), idMaquinaMySQL, getConsumoCPU(), getConsumoRam(), getConsumoDisco(), getNomeJanela(), getTemperatura()
                    );

                    conSQL.update("""
                        INSERT INTO registro(dataHora, fkMaquina, consumoCpu, consumoRam, consumoDisco, nomeJanela, temperatura) VALUES (?, ?, ?, ?, ?, ?, ?)
                        """, new Timestamp(data.getTime()), idMaquinaSQL, getConsumoCPU(), getConsumoRam(), getConsumoDisco(), getNomeJanela(), getTemperatura()
                    );

                    System.out.println("""
                *------------------------------------*
                |           Dados Coletados          |
                *------------------------------------*
                |Consumo da CPU: %.2f
                |Consumo da RAM: %.2f
                |Consumo da Disco: %.2f
                |Nome da Janela: %s
                |Temperatura: %.1f
                *------------------------------------*
                        """.formatted(getConsumoCPU(), getConsumoRam(), getConsumoDisco(), getNomeJanela(), getTemperatura()));

                    triggerRegistro(idMaquinaSQL, idMaquinaMySQL, trigger); // Pós inserção de registro dos voláteis
                }
            }, interval, interval);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Não foi encontrada nenhuma máquina vinculada a este usuário");
        }
    }

    private void triggerRegistro(Integer idMaquinaSQL,Integer idMaquinaMySQL, Limite trigger) {
        try {
                // Comparando limite com o consumo
                if ( trigger.getLimiteCPU() < consumoCPU ||
                        trigger.getLimiteRam() < consumoRam ||
                        trigger.getLimiteDisco() < consumoDisco
                ) {
                    // Caso consumo ultrapasse o limite
                    Date data = new Date(); // Data e hora do alerta
                    // Coletando id do registro mais recente
                    Integer fkRegistroMySQL = conMySQL.queryForObject("SELECT idRegistro FROM registro WHERE fkMaquina = ? ORDER BY idRegistro DESC LIMIT 1", Integer.class, idMaquinaMySQL);
                    Integer fkRegistroSQL = conSQL.queryForObject("SELECT TOP 1 idRegistro FROM registro WHERE fkMaquina = ? ORDER BY idRegistro DESC", Integer.class, idMaquinaSQL);

                    // Insert do alerta
                    conMySQL.update("INSERT INTO alerta(dataAlerta, fkRegistro) VALUES (?, ?)", new Timestamp(data.getTime()), fkRegistroMySQL);
                    conSQL.update("INSERT INTO alerta(dataAlerta, fkRegistro) VALUES (?, ?)", new Timestamp(data.getTime()), fkRegistroSQL);

                    System.out.println("""
                            ------------------
                            ALERTEI!!!!!!!!!!!
                            ------------------
                            """);
                }
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Erro no insert do trigger");
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

    public Double getTemperatura() {
        return temperatura;
    }

    public String getNomeJanela() {
        return nomeJanela;
    }

    @Override
    public String toString() {
        return "Registro{" +
                ", permanenciaDeDados=" + permanenciaDeDados +
                ", consumoCPU=" + consumoCPU +
                ", consumoRam=" + consumoRam +
                ", consumoDisco=" + consumoDisco +
                ", temperatura=" + temperatura +
                ", nomeJanela='" + nomeJanela + '\'' +
                '}';
    }
}
