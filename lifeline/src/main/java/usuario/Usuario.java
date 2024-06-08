package usuario;

import service.ConexaoMySQL;
import service.ConexaoSQL;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Usuario {
    //Instancias
    ConexaoMySQL conectar = new ConexaoMySQL();
    JdbcTemplate conMySQL = conectar.getConexao();
    ConexaoSQL conexaoSql = new ConexaoSQL();
    JdbcTemplate conSQL = conexaoSql.getConexao();
    //Atributos
    private Integer idUsuarioSQL;
    private Integer idUsuarioMySQL;
    private String nome;
    private String endereco;
    private String telefone;
    private String cargo;
    private String email;
    private String senha;
    private String cpf;
    private Integer fkEmpresa;

    //Construtor
    public Usuario(String email, String senha) {
        try {
            String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
            conSQL.queryForObject(sql, new Object[]{email, senha}, (resposta, indice) -> {
                this.idUsuarioSQL = resposta.getInt(1);
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

        try {
            Integer id = conMySQL.queryForObject("SELECT idUsuario FROM usuario WHERE email = ? AND senha = ? LIMIT 1", Integer.class, email, senha); // coletando id do usuario no banco
            this.idUsuarioMySQL = id;
        } catch (EmptyResultDataAccessException e) {
            Integer id = inserirUsuarioMySQL();
            this.idUsuarioMySQL = id;
        }
    }

    private Integer inserirUsuarioMySQL() {
        try {
            conMySQL.update("INSERT INTO usuario(nome, endereco, telefone, cargo, senha, email, cpf) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        getNome(), getEndereco(), getTelefone(), getCargo(), getSenha(), getEmail(), getCpf());
            return conMySQL.queryForObject("SELECT idUsuario FROM usuario WHERE email = ? AND senha = ? LIMIT 1", Integer.class, email, senha);
        } catch (DataAccessException d) {
                System.out.println("Não foi possivel inserir o usuário no bando de dados local");
                return null;
        }
    }

    public Integer getIdUsuarioSQL() {
        return idUsuarioSQL;
    }

    public Integer getIdUsuarioMySQL() {
        return idUsuarioMySQL;
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
        return "Usuario{" +
                "idUsuarioSQL=" + idUsuarioSQL +
                ", idUsuarioMySQL=" + idUsuarioMySQL +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cargo='" + cargo + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", cpf='" + cpf + '\'' +
                ", fkEmpresa=" + fkEmpresa +
                '}';
    }
}
