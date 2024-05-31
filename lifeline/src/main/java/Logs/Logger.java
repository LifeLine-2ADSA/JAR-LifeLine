package Logs;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final Integer limiteDeLinhas = 5;
    private static final String diretorio = "logs-2024";

    public static void escreverLogSucesso(String mensagem) {
        LocalDate hoje = LocalDate.now();
        String dataFormatada = hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String nomeDoArquivo = diretorio + File.separator + "Logger_Successes_%s.txt".formatted(dataFormatada);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomeDoArquivo, true));
            BufferedReader reader = new BufferedReader(new FileReader(nomeDoArquivo));
            LocalDateTime dataAtual = LocalDateTime.now();
            String dataAtualformatada =  dataAtual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            Integer linhas = 0;

                writer.append(dataAtualformatada).append(" : ");
                writer.append(mensagem).append("\n");
                writer.close();
        }catch (IOException e) {
            System.out.println("Erro ao gerar o log de mudanças" + e.getMessage());
        }
    }
    public static void escreverLogExceptions(String mensagem) {
        LocalDate hoje = LocalDate.now();
        String dataFormatada = hoje.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String nomeDoArquivo = "Logger_Exceptions_%s.txt".formatted(dataFormatada);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomeDoArquivo, true));
            LocalDateTime dataAtual = LocalDateTime.now();
            String dataAtualformatada =  dataAtual.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            writer.append(dataAtualformatada).append(" : ");
            writer.append(mensagem).append("\n");
            writer.close();
        }catch (IOException e) {
            System.out.println("Erro ao gerar o log de exceções" + e.getMessage());
        }
    }

    public static Integer getNumeroDaLinha() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }
    public static String getNomeDaClasse(Exception e) {
        return e.getStackTrace()[0].getClassName();
    }

    private static void criarDiretorioSeNaoExistir() {

    }


}
