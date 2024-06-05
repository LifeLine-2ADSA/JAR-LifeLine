package conexao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexaoMySQL {

    private JdbcTemplate conexao;

    public ConexaoMySQL() {
        BasicDataSource data = new BasicDataSource();
        data.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Conexao local
        data.setUrl("jdbc:mysql://localhost:3306/lifeline");
        data.setUsername("root");
        data.setPassword("215912");

        // Conexao container
        // Use o nome do serviço 'db' definido no docker-compose.yml como hostname
//         data.setUrl("jdbc:mysql://db:3306/lifeline");
//         data.setPassword("urubu100");
//         data.setUsername("root");


        conexao = new JdbcTemplate(data);
    }

    public JdbcTemplate getConexao() {
        return conexao;
    }
}
