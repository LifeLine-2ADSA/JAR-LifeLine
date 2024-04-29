package conexao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {

  private JdbcTemplate conexao;

  public Conexao() {
    BasicDataSource data = new BasicDataSource();
    data.setDriverClassName("com.mysql.cj.jdbc.Driver");
    data.setUrl("jdbc:mysql://localhost:3306/lifeline");
    data.setUsername("EU");
    data.setPassword("215912");

    conexao = new JdbcTemplate(data);
  }

  public JdbcTemplate getConexao() {
    return conexao;
  }
}
