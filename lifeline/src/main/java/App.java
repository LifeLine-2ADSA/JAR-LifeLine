import maquina.Maquina;
import usuario.Usuario;

public class App {
    public static void main(String[] args) {
        System.out.printf("""
                  
                  _     _   __        _     _           
                 | |   (_) / _| ___  | |   (_) _ _   ___
                 | |__ | ||  _|/ -_) | |__ | || ' \\ / -_)
                 |____||_||_|  \\___| |____||_||_||_|\\___|
                                                                                                                                                                            
                """);

        Usuario usuario = new Usuario("joao@techinnovations.com", "senha123");
        Maquina maquina = new Maquina();
        maquina.verificarMaquina(usuario.getIdUsuario());
    }
}
