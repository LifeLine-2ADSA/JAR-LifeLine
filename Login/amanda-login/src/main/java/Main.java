import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        Metodos metodoss = new Metodos();

        System.out.println("""
                -------------------------------------------
                        Bem vindo(a) ao LifeLine💙
                -------------------------------------------
                """);
        Integer opcao;
        do {
            System.out.println("""
                    \nO que deseja fazer:
                    1 - Cadastro.
                    2 - Login.
                    0 - Sair
                    """);
            opcao = leitor.nextInt();
            switch (opcao) {
                case 1:
                    System.out.println("Digite seu nome: ");
                    String nome = leitor.next();
                    System.out.println("Digite seu email: ");
                    String email = leitor.next();
                    System.out.println("Digite sua senha: ");
                    String senha = leitor.next();
                    System.out.println("Digite novamente sua senha: ");
                    String confirmaSenha = leitor.next();
                    System.out.println("Digite seu telefone: ");
                    String telefone = leitor.next();

                    Boolean cadastroRealizado = metodoss.cadastro(nome,email,senha,confirmaSenha, telefone);
                    // System.out.println(metodoss.cadastro(nome,email,senha,confirmaSenha, telefone));
                    System.out.println(cadastroRealizado ? "Cadastro realizado com sucesso" : "Erro! Tente cadastrar novamente");
                    break;
                case 2:
                    System.out.println("Digite seu email: ");
                    String emailLogin = leitor.next();
                    System.out.println("Digite sua senha: ");
                    String senhaLogin = leitor.next();

                    Boolean loginRealizado = metodoss.login(emailLogin,senhaLogin);
                    System.out.println(loginRealizado ? "Login efetuado com sucesso!" : "Email ou senha inválidos");
                    break;
            }


        } while (opcao > 0 && opcao < 2);
    }
}
