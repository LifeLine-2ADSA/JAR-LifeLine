package usuario;

import conexao.Conexao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Scanner;

public class Usuario {
    //Instancias
    Conexao conectar = new Conexao();
    JdbcTemplate con = conectar.getConexao();

    //Atributos
    private Integer idUsuario;
    private String nome;
    private String endereco;
    private String telefone;
    private String cargo;
    private String nomeEmpresa;
    private Boolean ti;
    private String email;
    private String senha;
    private String cpf;
    private Integer fkEmpresa;

    //Construtor
    public Usuario(String email, String senha) {
        this.ti = false;
        this.nomeEmpresa = this.checarEmpresa();
        try {
            String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
            con.queryForObject(sql, new Object[]{email, senha}, (resposta, indice) -> {
                this.idUsuario = resposta.getInt(1);
                this.nome = resposta.getString(2);
                this.endereco = resposta.getString(3);
                this.telefone = resposta.getString(4);
                this.cargo = resposta.getString(5);
                this.senha = resposta.getString(6);
                this.email = resposta.getString(7);
                this.cpf = resposta.getString(8);
                this.fkEmpresa = resposta.getInt(9);
                return null;
            });
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Email ou senha incorretos, tente novamente...");
        }
    }

    public String checarEmpresa() {
        try{
           return con.queryForObject("SELECT e.nome FROM usuario u JOIN empresa e on u.fkEmpresa = e.idEmpresa WHERE u.idUsuario = ?", String.class, getIdUsuario());
        }catch (EmptyResultDataAccessException e){
            System.out.println("Usuário não está vinculado a nenhuma empresa...");
        }
        return null;
    }


    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCargo() {
        return cargo;
    }

    public Boolean getTi() {
        return ti;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getCpf() {
        return cpf;
    }

    public Integer getFkEmpresa() {
        return fkEmpresa;
    }

    @Override
    public String toString() {
        return """
                id: %d
                nome: %s
                endereco: %s
                telefone: %s
                cargo: %s
                email: %s
                senha: %s
                cpf: %s
                fkEmpresa: %d
                """.formatted(idUsuario, nome, endereco, telefone, cargo, email, senha, cpf, fkEmpresa);
    }

}
