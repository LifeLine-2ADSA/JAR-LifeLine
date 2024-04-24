import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.janelas.JanelaGrupo;
import com.github.britooo.looca.api.group.processos.Processo;
import conexao.Conexao;
import maquina.Conversor;
import maquina.Maquina;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


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
        Looca looca = new Looca();
//
//        Double disco = 0.0;
//        for (int i = 0; i < looca.getGrupoDeDiscos().getVolumes().size(); i++) {
//            disco += Conversor.converterDoubleTresDecimais(Conversor.formatarBytes(looca.getGrupoDeDiscos().getVolumes().get(i).getTotal() - looca.getGrupoDeDiscos().getVolumes().get(i).getDisponivel()));
//        }
//        System.out.println(disco);
        //System.out.println(looca.getGrupoDeJanelas().getJanelasVisiveis());
        //looca.getGrupoDeJanelas().getJanelasVisiveis().get(1).getTitulo()
        List<Processo> lista = looca.getGrupoDeProcessos().getProcessos().stream().filter(processo -> processo.getUsoCpu().doubleValue() > 0.5).toList();
        System.out.println(lista);
        List<JanelaGrupo> lista2 = looca.getGrupoDeJanelas().getJanelasVisiveis().stream().filter().toList();
        System.out.println(lista2);
    }
}
