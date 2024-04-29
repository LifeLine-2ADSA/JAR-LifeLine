package usuario;

import org.springframework.dao.EmptyResultDataAccessException;

public class Suporte extends Usuario {

    private String matriz;
    private Boolean ti;

    public Suporte(String email, String senha) {
        super(email, senha);
        this.matriz = this.checarEmpresa();
        this.ti = true;
    }

    public String checarEmpresa() {
        try{
            return con.queryForObject("SELECT e.matriz FROM usuario u JOIN empresa e on u.fkEmpresa = e.idEmpresa WHERE u.idUsuario = ?", String.class, getIdUsuario());
        }catch (EmptyResultDataAccessException e){
            System.out.println("Usuário não está vinculado a nenhuma matriz...");
        }
        return null;
    }

    public String getMatriz() {
        return matriz;
    }

    @Override
    public Boolean getTi() {
        return ti;
    }

}
