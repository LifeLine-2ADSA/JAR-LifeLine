import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Login login = new Login();
        Scanner leitor = new Scanner(System.in);
        System.out.println("Digite o seu nome:");
        String nome = leitor.nextLine();
        login.verificarNomeUsuario(nome);
    }
}
