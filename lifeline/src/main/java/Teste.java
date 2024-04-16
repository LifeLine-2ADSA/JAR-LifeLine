import conexao.Conexao;
import maquina.Maquina;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;


public class Teste {
    public static void main(String[] args) {
        // SAO MEUS TESTE DOIDOS :)
//        Conexao conectar = new Conexao();
//        JdbcTemplate conec = conectar.getConexao();
//
//        try {
//            Maquina maquinaVerificacao = conec.queryForObject("SELECT * FROM maquina WHERE fkUsuario = ? AND macAddress = ?", new BeanPropertyRowMapper<>(Maquina.class), 1, "18:a6:f7:01:3c:2d");
//            if (maquinaVerificacao.getMacAddress() != null) {
//                System.out.println("Pegou algo");
//            }
//        } catch (EmptyResultDataAccessException e) {
//            System.out.println("vazio");
//        }
    }
}
