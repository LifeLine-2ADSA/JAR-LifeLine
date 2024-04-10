
    import java.util.Scanner;

    public class Main {

        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Bem-vindo a Life Line!");

            System.out.print("Usuário: ");
            String username = scanner.nextLine();
            System.out.print("Senha: ");
            String password = scanner.nextLine();
            
            if (metodosLogin.validateLogin(username, password)) {
                System.out.println("bem-vindo de volta!");
                // Coloque o código para o que fazer após o login bem-sucedido aqui
            } else {
                System.out.println("Usuário ou senha inválidos. Tente novamente.");
            }
        }
    }



