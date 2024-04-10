import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Login {
    void verificarNomeUsuario(String nomeInserido) {
        Scanner leitor =  new Scanner(System.in);
        List<String> nomeUsuario = new ArrayList<>();
        nomeUsuario.add("Amanda");
        nomeUsuario.add("Victor");
        nomeUsuario.add("Kely");
        nomeUsuario.add("Pedro");
        nomeUsuario.add("Carlos");
        nomeUsuario.add("Guedes");
        Integer indexUsuario = 0;
        String senhaInserida = "";
        for (int i = 0; i < nomeUsuario.size(); i++) {
            if(nomeInserido.equalsIgnoreCase(nomeUsuario.get(i))) {
                System.out.printf("Digite a senha para %s:\n", nomeInserido);
                senhaInserida = leitor.nextLine();
                indexUsuario = i;
                verificarSenhaUsuario(indexUsuario, senhaInserida);
                break;
            }else {
                if(i >= nomeUsuario.size() - 1){
                System.out.println("O nome inserido não foi encontrado tente novamente");
                    i = 0;
                    nomeInserido = leitor.nextLine();
            }
            }
        }
    }

    void verificarSenhaUsuario(Integer indice, String senha) {
        Scanner leitor =  new Scanner(System.in);
        List<String> senhaUsuario =  new ArrayList<>();
        senhaUsuario.add("CookieHJ?");
        senhaUsuario.add("Marmita123");
        senhaUsuario.add("Totoro1988");
        senhaUsuario.add("EuAmoPHP");
        senhaUsuario.add("amorVoltaPraMim");
        senhaUsuario.add("2O0Y0(2&8!!>:q1nnS/!!*¨512_1%PoYr*92@");
        Boolean senhaExiste = false;
        int tentativasSenha = 0;
        do {
            if (senha.equals(senhaUsuario.get(indice))){
                System.out.printf("Bem vindo(a) de volta!");
                senhaExiste = true;
            }else {
                System.out.println("Senha incorreta tente novamente: ");
                senha = leitor.nextLine();
                tentativasSenha ++;
                if(tentativasSenha > 2) {
                    System.out.println("Muitas tentativas tente novamente mais tarde");
                    break;
                }
            }
        }while (!senhaExiste);
    }
}
