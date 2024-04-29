import maquina.Limite;
import maquina.Maquina;
import maquina.Registro;
import usuario.Suporte;
import usuario.Usuario;
import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        Integer opcao;
        do {
            System.out.printf("""
                    *------------------------------------*
                    | Olá, Seja bem-vindo(a) a Life Line!|
                    *------------------------------------*
                    | Digite o que deseja realizar!      |
                    |                                    |
                    | 0 - Encerrar App                   |
                    | 1 - Login                          |
                    | 2 - Login (Suporte)                |
                    *------------------------------------*
                    """);
            opcao = leitor.nextInt();

            if (opcao == 0) {
                System.out.printf("Encerrando...");
            } else if (opcao == 1) {
                Boolean usuarioLogado = false;
                Usuario usuario = login();

                do {
                    if (usuario.getIdUsuario() != null) {
                        usuarioLogado = true;
                    } else {
                        usuario = login();
                    }
                } while (!usuarioLogado); // Login bem sucedido

                maquina(usuario); // Pos login
            } else if (opcao == 2){
                Boolean usuarioTI = false;
                Usuario usuario = login();

                do {
                    if (usuario.getIdUsuario() != null
                            && usuario.getCargo().toLowerCase().contains("TI".toLowerCase())) {
                        usuarioTI = true;
                    } else {
                        usuario = login();
                    }
                } while (!usuarioTI); // Login bem sucedido
                do {
                System.out.printf("""
                    *------------------------------------*
                    | Olá, Seja bem-vindo(a) %s!
                    *------------------------------------*
                    | Digite o que deseja realizar!      |
                    |                                    |
                    | 0 - Checar integridade do sistema  |
                    | 1 - Voltar                          |
                    *------------------------------------*
                    """);
                    opcao = leitor.nextInt();
                    if(opcao == 0) {
                        System.out.println("Ok, até mais");
                    } else if(opcao == 1) {
                        try {
                            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "~/LifeLine/JAR-LifeLine-Gabriel/databaseTest.sh");
                            Process p = pb.start();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } while (opcao != 0);
            } else {
                System.out.println("Digite uma opção válida!");
            }
        } while (opcao != 1 && opcao != 0 && opcao != 2);
    }

    private static Usuario login() {
        Scanner leitor = new Scanner(System.in);
            System.out.printf("""
                *------------------------------------*
                |        Login - Life Line           |
                *------------------------------------*
                |Digite o seu email:                 |
                *------------------------------------*
                """
            );
            String email = leitor.next();
            System.out.printf("""
                *------------------------------------*
                |Digite a sua senha:                 |
                *------------------------------------*
                """
            );
            String senha = leitor.next();
            return new Usuario(email, senha);
    }

    private static Suporte loginTI() {
        Scanner leitor = new Scanner(System.in);
        System.out.printf("""
                *------------------------------------*
                |        Login - Life Line           |
                *------------------------------------*
                |Digite o seu email:                 |
                *------------------------------------*
                """
        );
        String email = leitor.next();
        System.out.printf("""
                *------------------------------------*
                |Digite a sua senha:                 |
                *------------------------------------*
                """
        );
        String senha = leitor.next();
        return new Suporte(email, senha);
    }

    private static void maquina(Usuario usuario) {
        Maquina maquinaUsuario = new Maquina();

        if (!maquinaUsuario.verificarMaquina(usuario.getIdUsuario())) { // Caso maquina não foi identificada
            maquinaUsuario.cadastrarMaquina(usuario.getIdUsuario());
        }
        Limite limite = new Limite(maquinaUsuario.getIdMaquina());
        while (true) {
            Registro registro = new Registro();
            registro.inserirRegistros(maquinaUsuario.getIdMaquina(), limite);
        }
    }
}
