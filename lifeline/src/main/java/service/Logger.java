package service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Logger {
    private static final int LIMITE_LINHAS = 1500;
    private static final Path LOG_DIR = Paths.get(System.getProperty("user.home"), "Documents");
    private static final String PREFIXO_LOG = "Logger_";
    private static final String DATA_FORMATADA = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    private static final String DATA_ATUAL_FORMATADA = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    private static final String SUFIXO_LOG = ".txt";
    private static final Integer NUMERO_DA_LINHA = Thread.currentThread().getStackTrace()
                                                    [2].getLineNumber();
    private static final String NOME_DA_CLASSE = Thread.currentThread().getStackTrace()
                                                    [2].getClassName();

    public static void escreverLog(String message, TipoLog tipoLog) {
        Path arquivo = criarArquivoDeLog();
        if (arquivo == null ) return;
        int lines = (Files.exists(arquivo)) ? contarLinhas(arquivo) : 0;
        try (BufferedWriter writer = Files.newBufferedWriter(arquivo, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            String logMessage = String.format("%s [%s] LINHA: %d CLASSE: %S: %s%n", DATA_ATUAL_FORMATADA, tipoLog, NUMERO_DA_LINHA, NOME_DA_CLASSE, message);
            writer.write(logMessage);

            if (++lines >= LIMITE_LINHAS) {
                renomearLogs(arquivo);
            }
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    private static Path criarArquivoDeLog() {
        try {
            Files.createDirectories(LOG_DIR);
            String fileName = PREFIXO_LOG + DATA_FORMATADA + SUFIXO_LOG;
            return LOG_DIR.resolve(fileName);
        } catch (IOException e) {
            System.out.println("Erro ao criar o arquivo de log: " + e);
        }
        return null;
    }

    private static int contarLinhas(Path log) {
        try (Stream<String> numeroDelinhas = Files.lines(log)){
            return numeroDelinhas.toList().size();
        } catch (IOException e) {
            System.err.println("Erro ao contar as linhas do arquivo log: " + e);
        }
        return 0;
    }

    public static List<File> getLogs() {
        List<File> logs = new ArrayList<>();
        try (DirectoryStream<Path> streamLogs = Files.newDirectoryStream(LOG_DIR)){
            streamLogs.forEach(log -> {if (Files.isRegularFile(log)) logs.add(log.toFile());});
        }catch (IOException ioException) {
            System.err.println("Erro ao retornar os arquivos em %s: ".formatted(LOG_DIR) + ioException);
        }
        return logs;
    }

    private static void renomearLogs(Path logAtual) {
        try {
            int quantidadeDeLogs = getLogs().size();
            Path logsRenomeados = LOG_DIR.resolve(PREFIXO_LOG + DATA_FORMATADA + "-" + quantidadeDeLogs + SUFIXO_LOG);
            Files.move(logAtual, logsRenomeados);
        } catch (IOException ioException) {
            System.out.println("Erro em renomear o log: " + ioException);
        }
    }

}
