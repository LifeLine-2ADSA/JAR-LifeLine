package maquina;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.processos.Processo;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import conexao.Conexao;
import conexao.ConexaoSql;
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
    ConexaoSql conexaoSQL = new ConexaoSql();
    JdbcTemplate conSQL = conexaoSQL.getConexaosql();
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
//            if (processoMaisConsome.getBytesUtilizados() < processo.getBytesUtilizados()) {
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
        this.consumoCPU = Conversor.converterDoubleDoisDecimais(looca.getProcessador().getUso());
        this.consumoRam = Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getMemoria().getEmUso()));
        this.consumoDisco = disco;
    }

    public void inserirRegistros(Integer idMaquina, Limite trigger) {

        try {
            // Loop para registrar o dados do recurso ao banco
                permanenciaDeDados.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Date data = new Date(); // data da coleta
                        // insert ao banco
//                        conec.update("""
//                                INSERT INTO registro(dataHora, fkMaquina, consumoCpu, consumoRam, consumoDisco, nomeJanela, temperatura) VALUES (?, ?, ?, ?, ?, ?, ?)
//                                """, new Timestamp(data.getTime()), idMaquina, getConsumoCPU(),getConsumoRam(), getConsumoDisco(),getNomeJanela(),getTemperatura()
//                                );
                        conSQL.update("""
                                INSERT INTO registro(dataHora, fkMaquina, consumoCpu, consumoRam, consumoDisco, nomeJanela, temperatura) VALUES (?, ?, ?, ?, ?, ?, ?)
                                """, new Timestamp(data.getTime()), idMaquina, getConsumoCPU(),getConsumoRam(), getConsumoDisco(),getNomeJanela(),getTemperatura()
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
                                """.formatted(getConsumoCPU(), getConsumoRam(),getConsumoDisco(),getNomeJanela(),getTemperatura()));
                        triggerRegistro(idMaquina, trigger); // Pos inserção de registro dos volateis
                    }
                },0, 50000);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Não foi encontrada nenhuma máquina vinculada a este usuário");
        }
    }

    private void triggerRegistro(Integer idMaquina, Limite trigger) {
        try {
                // Comparando limite com o consumo
                if ( trigger.getLimiteCPU() < consumoCPU ||
                        trigger.getLimiteRam() < consumoRam ||
                        trigger.getLimiteDisco() < consumoDisco
                ) {
                    // Caso consumo ultrapasse o limite
                    Date data = new Date(); // Data e hora do alerta
                    // Coletando id do registro mais recente
                    Integer fkRegistro = conSQL.queryForObject("SELECT TOP 1 idRegistro FROM registro WHERE fkMaquina = ? ORDER BY idRegistro DESC", Integer.class, idMaquina);

                    // Insert do alerta
                    // conec.update("INSERT INTO alerta(dataAlerta, fkRegistro) VALUES (?, ?)", new Timestamp(data.getTime()), fkRegistro);
                    conSQL.update("INSERT INTO alerta(dataAlerta, fkRegistro) VALUES (?, ?)", new Timestamp(data.getTime()), fkRegistro);

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
