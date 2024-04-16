import maquina.Maquina;
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
            System.out.printf("""
                    *------------------------------------*
                    | Olá, Seja bem-vindo(a) a Life Line!|
                    *------------------------------------*
                    |Digite o seu email:                 |
                    *------------------------------------*
                    """
            );
            String email = leitor.next();
                System.out.printf("""
                    *------------------------------------*
                    |Digite o sua senha:                 |
                    *------------------------------------*
                    """
                );
            String senha = leitor.next();

            Usuario usuario = new Usuario(email, senha);
            Maquina maquinaUsuario = new Maquina();

            if (!maquinaUsuario.verificarMaquina(usuario.getIdUsuario())) {
                System.out.printf(
                        """
                    *------------------------------------*
                    |Digite o nome para a maquina:       |
                    *------------------------------------*
                                """
                );
                String nomeMaquina = leitor.nextLine();
                maquinaUsuario.cadastrarMaquina(nomeMaquina, usuario.getIdUsuario());
            } else {
                // Quando já tem a maquina cadastrada
            }
            } else {
                System.out.println("Digite uma opção válida!");
            }
        } while (opcao != 1 && opcao != 0);
    }
}
