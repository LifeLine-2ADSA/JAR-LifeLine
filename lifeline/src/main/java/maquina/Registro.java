package maquina;

import com.github.britooo.looca.api.core.Looca;
import org.json.JSONObject;
import service.ConexaoMySQL;
import service.ConexaoSQL;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import service.Conversor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    private String hostname;

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
        this.hostname = looca.getRede().getParametros().getHostName();
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
                |Consumo da CPU: %.2f GHz
                |Consumo da RAM: %.2f Gb
                |Consumo da Disco: %.2f Gb
                |Nome da Janela: %s
                |Temperatura: %.1f °C
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


                    enviarAlertaSlack(); // Envia alerta para o Slack

                }
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Erro no insert do trigger");
        }
    }


    private void enviarAlertaSlack() {
        try {
            // Montando o conteúdo da mensagem
            JSONObject json = new JSONObject();
            json.put("text", """
                            *------------------------------------*
                            |  A maquina: %s está em ALERTA!     |
                            *------------------------------------*
                            |Consumo da CPU: %.2f GHz
                            |Consumo da RAM: %.2f Gb
                            |Consumo da Disco: %.2f Gb
                            |Nome da Janela: %s
                            |Temperatura: %.1f °C
                            *------------------------------------*
                            """.formatted(getHostname(), getConsumoCPU(), getConsumoRam(), getConsumoDisco(), getNomeJanela(), getTemperatura()));

            // Enviando mensagem para o Slack
            Slack.sendMessage(json);
        } catch (IOException | InterruptedException e) {
            System.out.println("Erro ao enviar alerta para o Slack: " + e.getMessage());
        }
    }




    static class Slack {
        private static HttpClient client = HttpClient.newHttpClient();
        private static final String URL = "https://hooks.slack.com/services/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";

        static void sendMessage(JSONObject content) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder(
                            URI.create(URL))
                    .header("accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(String.format("Status: %s", response.statusCode()));
            System.out.println(String.format("Response: %s", response.body()));
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

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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
