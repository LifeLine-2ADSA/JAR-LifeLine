import maquina.Maquina;
import usuario.Usuario;

public class App {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Erro na autenticação!");
            return;
        }

        System.out.printf("""
                      
                      _     _   __        _     _           
                     | |   (_) / _| ___  | |   (_) _ _   ___
                     | |__ | ||  _|/ -_) | |__ | || ' \\ / -_)
                     |____||_||_|  \\___| |____||_||_||_|\\___|
                                                                                                                                                                                
                    """);

        Usuario usuario = new Usuario(args[0], args[1]);
        Maquina maquina = new Maquina();
        maquina.verificarMaquina(usuario.getIdUsuarioSQL(), usuario.getIdUsuarioMySQL());
    }
}
