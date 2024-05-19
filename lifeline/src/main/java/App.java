import maquina.Limite;
import maquina.Maquina;
import maquina.Registro;
import usuario.Usuario;

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
            } else {
                System.out.println("Digite uma opção válida!");
            }
        } while (opcao != 1 && opcao != 0);
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

    private static void maquina(Usuario usuario) {
        Maquina maquinaUsuario = new Maquina();
        maquinaUsuario.verificarMaquina(usuario.getIdUsuario());
    }
}
