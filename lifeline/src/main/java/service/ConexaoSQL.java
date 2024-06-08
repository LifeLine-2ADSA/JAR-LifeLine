package service;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexaoSQL extends Conexao{

    public ConexaoSQL(){
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl("jdbc:sqlserver://100.27.91.182:1433;" +
                "database=lifeline;" +
                "trustServerCertificate=true;");
        dataSource.setUsername("sa");
        dataSource.setPassword("urubu_100");



        conexao = new JdbcTemplate(dataSource);
    }

    @Override
    public JdbcTemplate getConexao() {
        return conexao;
    }
}