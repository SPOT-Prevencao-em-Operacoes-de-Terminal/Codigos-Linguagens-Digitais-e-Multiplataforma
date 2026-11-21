
package sptech.scholl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class GerenciadorMonitoramento {

    private JdbcTemplate jdbcTemplate;

    public List<String> buscarEmailsUsuariosAtivos(Integer idServidor) {

        String sql = """
            SELECT DISTINCT c.contatocol
            FROM servidor s
            JOIN usuario u
                ON u.fk_empresa = s.fk_empresa
            JOIN contato c
                ON c.fk_usuario = u.id_usuario
            WHERE s.id_servidor = ?
              AND u.status = 'Ativo'
              AND c.fk_tipo_contato = 2
            """;

        return jdbcTemplate.queryForList(
                sql,
                String.class,
                idServidor
        );
    }

    public GerenciadorMonitoramento(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Identifica o servidor pelo UUID

    public Integer obterIdServidor(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException(
                    "Não foi possível identificar o UUID desta máquina."
            );
        }
        String sql = "SELECT id_servidor FROM servidor WHERE UUID = ?";
        List<Integer> servidores = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getInt("id_servidor"),
                uuid
        );

        if (servidores.isEmpty()) {
            throw new IllegalStateException(
                    "Esta máquina não está cadastrada no SPOT. " +
                            "Solicite ao administrador o cadastro do servidor."
            );
        }

        return servidores.get(0);
    }



    // Verifica se a rede possui limite configurado
    public boolean verificarLimites(int idServidor) {

        Integer quantidade = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM configuracao " +
                        "WHERE fk_servidor = ? AND fk_componente = ?",
                Integer.class,
                idServidor,
                4
        );

        return quantidade != null && quantidade > 0;
    }

    // Cadastra os limites padrão que estiverem faltando
    public void cadastrarLimitesPadrao(int idServidor) {

        int[] componentes = {1, 2, 3, 4};
        double[] limites = {70.0, 80.0, 85.0, 50.0};

        for (int i = 0; i < componentes.length; i++) {

            Integer quantidade = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM configuracao " +
                            "WHERE fk_servidor = ? AND fk_componente = ?",
                    Integer.class,
                    idServidor,
                    componentes[i]
            );

            if (quantidade == null || quantidade == 0) {

                String sql = "INSERT INTO configuracao " +
                        "(fk_servidor, fk_componente, limite) " +
                        "VALUES (?, ?, ?)";

                jdbcTemplate.update(
                        sql,
                        idServidor,
                        componentes[i],
                        limites[i]
                );

                System.out.println(
                        "Limite padrão cadastrado para o componente "
                                + componentes[i]
                );
            }
        }
    }

    // Busca o limite de um componente
    // Busca o limite de um componente
    public Double buscarLimite(int idServidor, int idComponente) {

        String sql = "SELECT limite FROM configuracao " +
                "WHERE fk_servidor = ? AND fk_componente = ?";

        List<Double> limites = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getDouble("limite"),
                idServidor,
                idComponente
        );

        if (limites.isEmpty()) {
            throw new IllegalStateException(
                    "Não existe limite configurado para este componente."
            );
        }

        return limites.get(0);
    }

    // Insere uma leitura e retorna seu ID
    public Long inserirLeitura(
            Double valor,
            int idComponente,
            int idServidor
    ) {

        String sql = "INSERT INTO leitura " +
                "(valor, dia_hora, fk_configComponente, fk_configServidor) " +
                "VALUES (?, NOW(), ?, ?)";

        KeyHolder chave = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            ps.setDouble(1, valor);
            ps.setInt(2, idComponente);
            ps.setInt(3, idServidor);

            return ps;

        }, chave);

        if (chave.getKey() == null) {
            throw new IllegalStateException(
                    "Não foi possível recuperar o ID da leitura."
            );
        }

        return chave.getKey().longValue();
    }

    // Classifica o valor de acordo com o limite
    public GrauAlerta verificarGrauAlerta(Double valor, Double limite) {

        if (valor >= limite) {
            return GrauAlerta.CRITICO;

        } else if (valor >= limite * 0.75) {
            return GrauAlerta.ATENCAO;

        } else {
            return GrauAlerta.NORMAL;
        }
    }

    // Registra o alerta crítico no banco
    public void registrarAlerta(GrauAlerta grau, Long idLeitura) {
        if (grau == GrauAlerta.CRITICO) {

            String sql = "INSERT INTO alerta (grau_alerta, fk_leitura) VALUES (?, ?)";

            jdbcTemplate.update(sql, "Crítico", idLeitura);
        }
    }
}
