import maquina.Maquina;
import usuario.Usuario;

import java.util.Scanner;

public class Teste {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("email");
        String email = leitor.next();
        System.out.println("senha");
        String senha = leitor.next();
        Usuario usuario = new Usuario(email, senha);
        System.out.println(usuario);
    }
}
