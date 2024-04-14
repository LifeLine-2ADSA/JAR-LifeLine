
import com.github.britooo.looca.api.core.Looca;
import maquina.Maquina;
import maquina.Registro;
import usuario.Usuario;

import java.util.Scanner;

public class Teste {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        System.out.println("email");
        String email = leitor.nextLine();
        System.out.println("senha");
        String senha = leitor.nextLine();
        Usuario usuario = new Usuario(email, senha);
        System.out.println(usuario);

        if (usuario.getIdUsuario() != null) {
            Integer idUsuario = usuario.getIdUsuario();
            System.out.println("nome da maquina");
            String nome = leitor.nextLine();
            Maquina maquina = new Maquina();
            if (nome == null || nome.isEmpty()) {
                System.out.println("Nome inválido");
            } else {
                maquina.cadastrarMaquina(email, nome, idUsuario);
            }
            // maquina.listarMaquinas(idUsuario);
            Registro registro = new Registro();
            System.out.println("""
                    Inserir dados:
                    1 - sim
                    2 - sim
                    3 - yes
                    """);
            registro.inserirRegistros(idUsuario);
        }

    }
}
