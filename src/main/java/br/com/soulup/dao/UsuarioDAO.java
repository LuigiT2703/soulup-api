package br.com.soulup.dao;

import br.com.soulup.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a tabela SOULUP_USUARIO.
 * A pontuacao e calculada na consulta: LEFT JOIN com os registros e SUM dos pontos das acoes.
 * O LEFT JOIN garante que usuarios sem nenhuma acao tambem aparecam (com 0 pontos).
 */
public class UsuarioDAO {

    private static final String SELECT_COM_PONTOS =
            "SELECT u.id_usuario, u.nome, u.email, u.dt_cadastro, " +
            "       COUNT(r.id_registro) AS total_acoes, " +
            "       NVL(SUM(a.pontos), 0) AS pontos " +
            "  FROM SOULUP_USUARIO u " +
            "  LEFT JOIN SOULUP_REGISTRO_ACAO r ON r.id_usuario = u.id_usuario " +
            "  LEFT JOIN SOULUP_ACAO a ON a.id_acao = r.id_acao ";

    private static final String GROUP_BY = " GROUP BY u.id_usuario, u.nome, u.email, u.dt_cadastro ";

    public List<Usuario> listar() throws SQLException {
        String sql = SELECT_COM_PONTOS + GROUP_BY + " ORDER BY u.id_usuario";
        return consultarLista(sql);
    }

    /** Ranking: maior pontuacao primeiro; empate desempata pelo nome. */
    public List<Usuario> listarRanking() throws SQLException {
        String sql = SELECT_COM_PONTOS + GROUP_BY + " ORDER BY pontos DESC, u.nome";
        return consultarLista(sql);
    }

    public Usuario buscarPorId(long id) throws SQLException {
        String sql = SELECT_COM_PONTOS + " WHERE u.id_usuario = ? " + GROUP_BY;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    /** Insere e devolve o id gerado pelo IDENTITY do Oracle. */
    public long inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO SOULUP_USUARIO (nome, email) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_usuario"})) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getLong(1);
            }
        }
    }

    public boolean atualizar(long id, Usuario usuario) throws SQLException {
        String sql = "UPDATE SOULUP_USUARIO SET nome = ?, email = ? WHERE id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(long id) throws SQLException {
        String sql = "DELETE FROM SOULUP_USUARIO WHERE id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Verifica se ja existe OUTRO usuario com o mesmo nome (ignorando o id informado). */
    public boolean existeNome(String nome, long idIgnorar) throws SQLException {
        return existe("SELECT 1 FROM SOULUP_USUARIO WHERE UPPER(nome) = UPPER(?) AND id_usuario <> ?", nome, idIgnorar);
    }

    /** Verifica se ja existe OUTRO usuario com o mesmo e-mail (ignorando o id informado). */
    public boolean existeEmail(String email, long idIgnorar) throws SQLException {
        return existe("SELECT 1 FROM SOULUP_USUARIO WHERE LOWER(email) = LOWER(?) AND id_usuario <> ?", email, idIgnorar);
    }

    private boolean existe(String sql, String valor, long idIgnorar) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setLong(2, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private List<Usuario> consultarLista(String sql) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    /** Converte a linha atual do ResultSet em um objeto Usuario. */
    private Usuario montar(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("id_usuario"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getDate("dt_cadastro").toLocalDate(),
                rs.getInt("total_acoes"),
                rs.getInt("pontos"));
    }
}
