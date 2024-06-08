import service.Logger;
import service.TipoLog;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Teste {
    private static Integer idUsuario;
    private static String nome;
    private static String endereco;
    private static String telefone;
    private static String cargo;
    private static String email;
    private static String senha;
    private static String cpf;
    private static Integer fkEmpresa;


    private static final String hostname = System.getProperty("user.name");
    private static final String so = System.getProperty("os.name");
    private static final Path path = (so.toLowerCase().contains("nix") || so.toLowerCase().contains("nux"))
            ? Paths.get("/home/%s/LifeLine/Logs".formatted(hostname))
            : Paths.get("C:\\Users\\%s\\Documents\\LifeLine\\Logs".formatted(hostname));
    private static final File diretorio = new File(path.toString());
    //Construtor

    public static void main(String[] args) {
       // PULANDO LOGIN
//        Usuario usuario = new Usuario("joao@techinnovations.com", "senha123");
//        Maquina maquina = new Maquina();
//
//        maquina.verificarMaquina(usuario.getIdUsuario());
//
        for (int i = 0; i <= 1500; i++) {
            try {
                for (int j = 0; j < 5; j++) {
                    Logger.escreverLog("10 dividido por %d é ".formatted(i) + 10/j, TipoLog.INFO);
                }
            }catch (ArithmeticException e) {
                Logger.escreverLog(e.toString(), TipoLog.UPDATE);
            }
        }

// AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA TO FICANDO LOUCO
//        List<File> logs = new ArrayList<>(Arrays.asList(diretorio.listFiles()));
//        File ultimoArquivo = logs.get(0);
//        for (File logr : logs) {
//            System.out.println("NOMEMEEE " + logr.getName());
//            if(logr.getName().indexOf("-") != 6) break;
//            int maiorNumero = 0;
//            int dashIndex = logr.getName().indexOf('-');
//            System.out.println(dashIndex);
//            int underscoreIndex = logr.getName().indexOf('_');
//            System.out.println(underscoreIndex);
//            // Extract the substring between '-' and '_'
//            String numberString = logr.getName().substring(dashIndex + 1, underscoreIndex);
//
//            // Convert the substring to an integer
//            int number = Integer.parseInt(numberString);
//            System.out.println("Numero" + number);
//            if (number > maiorNumero) {
//                maiorNumero = number;
//                ultimoArquivo = logr;
//            }
//        }
//        System.out.println("ultimo arquivo " + ultimoArquivo);;
//        System.out.println(logs.size());
        //        ConexaoSQL conexaoSQL = new ConexaoSQL();
//        JdbcTemplate conSQL = conexaoSQL.getConexao();
//        Maquina maquina = new Maquina();
//        System.out.println(maquina.getHostname());
        // SAO MEUS TESTE DOIDOS :)
//        Conexao conectar = new Conexao();
//        JdbcTemplate conec = conectar.getConexao();
//
//        try {
//            Maquina maquinaVerificacao = conec.queryForObject("SELECT * FROM maquina WHERE fkUsuario = ? AND macAddress = ?", new BeanPropertyRowMapper<>(Maquina.class), 1, "18:a6:f7:01:3c:2d");
//            if (maquinaVerificacao.getMacAddress() != null) {
//                System.out.println("Pegou algo");
//            }
//        } catch (EmptyResultDataAccessException e) {
//            System.out.println("vazio");
//        }
//        Looca looca = new Looca();

//        System.out.println(looca.getRede().getParametros().getHostName());

//        Double disco = 0.0;
//        for (int i = 0; i < looca.getGrupoDeDiscos().getVolumes().size(); i++) {
//            disco += Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getGrupoDeDiscos().getVolumes().get(i).getTotal() - looca.getGrupoDeDiscos().getVolumes().get(i).getDisponivel()));
//        }
//        System.out.println(disco);
        //System.out.println(looca.getGrupoDeJanelas().getJanelasVisiveis());
        //looca.getGrupoDeJanelas().getJanelasVisiveis().get(1).getTitulo()
//        List<Processo> lista = looca.getGrupoDeProcessos().getProcessos().stream().filter(processo -> processo.getUsoCpu().doubleValue() > 0.5).toList();
//        System.out.println(lista);

//        List<Janela> lista2 = looca.getGrupoDeJanelas().getJanelasVisiveis().stream().filter(janela -> janela.getPid().equals(20020)).toList();
//        System.out.println(lista2);

//        Processo processoMaisConsome = looca.getGrupoDeProcessos().getProcessos().get(0);
//
//        for (Processo processo : looca.getGrupoDeProcessos().getProcessos()) {
//            if (processoMaisConsome.getBytesUtilizados() < processo.getBytesUtilizados()) {
//                processoMaisConsome = processo;
//            }
//        }
//
//        Janela janelaProcesso = looca.getGrupoDeJanelas().getJanelas().get(0);
//
//        for (Janela janela : looca.getGrupoDeJanelas().getJanelas()) {
//            if (processoMaisConsome.getPid() == janela.getPid().intValue()) {
//                janelaProcesso = janela;
//            }
//        }
//
//        System.out.println(janelaProcesso.getTitulo());

//        System.out.println(looca.getTemperatura().getTemperatura().doubleValue());

    }
}
