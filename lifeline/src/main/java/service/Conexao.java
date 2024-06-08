package service;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class Conexao {

    protected JdbcTemplate conexao;

    public abstract JdbcTemplate getConexao();

}
