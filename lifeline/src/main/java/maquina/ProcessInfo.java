package maquina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessInfo {

    /**
     * Método principal para obter o processo que mais consome memória e, se disponível, o título da janela correspondente.
     * Detecta o sistema operacional e chama o método apropriado.
     *
     * @return String com o título da janela ou nome do processo.
     * @throws Exception se ocorrer um erro durante a execução dos comandos.
     */
    public static String getTopProcessAndWindow() throws Exception {
        // Detecta o sistema operacional
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Se o sistema operacional for Windows, executa a função correspondente
            return findTopProcessAndWindowWindows();
        } else if (os.contains("nix") || os.contains("nux")) {
            // Se o sistema operacional for Linux, executa a função correspondente
            return findTopProcessAndWindowLinux();
        } else {
            throw new UnsupportedOperationException("Sistema operacional não suportado.");
        }
    }

    /**
     * Encontra o processo que mais consome memória no Windows e obtém o título da janela correspondente.
     *
     * @return String com o título da janela ou nome do processo.
     * @throws Exception se ocorrer um erro durante a execução dos comandos.
     */
    private static String findTopProcessAndWindowWindows() throws Exception {
        // Executa o comando 'tasklist' para listar os processos no Windows
        Process process = Runtime.getRuntime().exec("tasklist /fo csv /nh");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        String topProcessName = "";
        int topProcessPid = -1;
        long maxMemoryUsage = -1;

        // Lê a saída do comando linha por linha
        while ((line = reader.readLine()) != null) {
            // Divide a linha em colunas
            String[] details = line.split("\",\"");
            // Obtém o nome do processo, PID e uso de memória
            String processName = details[0].replace("\"", "");
            int pid = Integer.parseInt(details[1].replace("\"", ""));
            long memoryUsage = parseMemoryUsage(details[4].replace("\"", ""));

            // Atualiza o processo que mais consome memória, se necessário
            if (memoryUsage > maxMemoryUsage) {
                maxMemoryUsage = memoryUsage;
                topProcessName = processName;
                topProcessPid = pid;
            }
        }

        // Executa o comando 'tasklist /v' para listar os processos com mais detalhes, incluindo o título da janela
        process = Runtime.getRuntime().exec("tasklist /v /fo csv /nh");
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // Lê a saída do comando linha por linha
        while ((line = reader.readLine()) != null) {
            // Divide a linha em colunas
            String[] details = line.split("\",\"");
            int pid = Integer.parseInt(details[1].replace("\"", ""));
            String windowTitle = details[8].replace("\"", "");

            // Se o PID da janela corresponder ao PID do processo que mais consome, retorna o título da janela
            if (pid == topProcessPid) {
                return windowTitle;
            }
        }

        // Se o título da janela não for encontrado, retorna o nome do processo
        return topProcessName;
    }

    /**
     * Encontra o processo que mais consome memória no Linux e obtém o título da janela correspondente.
     *
     * @return String com o título da janela ou nome do processo.
     * @throws Exception se ocorrer um erro durante a execução dos comandos.
     */
    private static String findTopProcessAndWindowLinux() throws Exception {
        // Executa o comando 'ps' para listar os processos no Linux, ordenados pelo uso de memória
        Process process = Runtime.getRuntime().exec("ps -eo pid,comm,rss --sort=-rss");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        reader.readLine(); // Ignora o cabeçalho
        String topProcessName = "";
        int topProcessPid = -1;

        // Lê a primeira linha (processo que mais consome memória)
        if ((line = reader.readLine()) != null) {
            // Divide a linha em colunas
            String[] details = line.trim().split("\\s+");
            topProcessPid = Integer.parseInt(details[0]);
            topProcessName = details[1];
        }

        try {
            // Executa o comando 'xprop' para obter a lista de IDs das janelas
            process = Runtime.getRuntime().exec("xprop -root _NET_CLIENT_LIST");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            line = reader.readLine();
            if (line == null) {
                throw new IOException("Comando xprop não retornou saída.");
            }

            String[] windowIds = line.split("#")[1].trim().split(", ");
            // Para cada ID de janela, obtém o PID do processo e o título da janela
            for (String windowId : windowIds) {
                process = Runtime.getRuntime().exec("xprop -id " + windowId + " _NET_WM_PID _NET_WM_NAME");
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                int pid = -1;
                String windowTitle = "";

                // Lê a saída do comando linha por linha
                while ((line = reader.readLine()) != null) {
                    if (line.contains("_NET_WM_PID")) {
                        pid = Integer.parseInt(line.split("=")[1].trim());
                    }
                    if (line.contains("_NET_WM_NAME")) {
                        windowTitle = line.split("=")[1].trim().replace("\"", "");
                    }
                }

                // Se o PID da janela corresponder ao PID do processo que mais consome, retorna o título da janela
                if (pid == topProcessPid) {
                    return windowTitle;
                }
            }

            // Se o título da janela não for encontrado, retorna o nome do processo
            return topProcessName;
        } catch (IOException e) {
            // Se ocorrer um erro ao executar 'xprop', retorna o nome do processo
            return topProcessName;
        }
    }

    /**
     * Converte o uso de memória de string para long, removendo caracteres indesejados.
     *
     * @param memoryUsage Uso de memória como string.
     * @return Uso de memória como long.
     */
    private static long parseMemoryUsage(String memoryUsage) {
        // Remove caracteres indesejados
        memoryUsage = memoryUsage.replace(",", "").replace(".", "").replace(" K", "").trim();
        // Se o valor for "N/A", retorna 0
        if (memoryUsage.equalsIgnoreCase("N/A")) {
            return 0L;
        }
        try {
            // Tenta converter o valor para long
            return Long.parseLong(memoryUsage);
        } catch (NumberFormatException e) {
            // Se ocorrer uma exceção, retorna 0
            return 0L;
        }
    }
}
