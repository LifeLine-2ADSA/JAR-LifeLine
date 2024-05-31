package usuario;

import Logs.Logger;
import conexao.Conexao;
import conexao.ConexaoSql;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Usuario {
    //Instancias
    Conexao conectar = new Conexao();
    JdbcTemplate con = conectar.getConexao();
    ConexaoSql conexaoSql = new ConexaoSql();
    JdbcTemplate conSQL = conexaoSql.getConexaosql();
    //Atributos
    private Integer idUsuario;
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
//        catch (CannotGetJdbcConnectionException eSQL){
//            Logger.escreverLogExceptions("Não foi possivel estabelecer conexão na linha %s da Classe %s: %s"
//                    .formatted(Logger.getNumeroDaLinha(), Logger.getNomeDaClasse(eSQL),eSQL));
//        }

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
