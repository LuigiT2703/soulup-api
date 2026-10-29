package br.com.soulup.dao;

import br.com.soulup.model.RegistroAcao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a tabela SOULUP_REGISTRO_ACAO.
 * O JOIN com SOULUP_ACAO traz o nome e os pontos da acao para o front exibir.
 */
public class RegistroAcaoDAO {

    private static final String SELECT =
            "SELECT r.id_registro, r.id_usuario, r.id_acao, a.nome AS nome_acao, a.pontos, r.dt_registro " +
            "  FROM SOULUP_REGISTRO_ACAO r " +
            "  JOIN SOULUP_ACAO a ON a.id_acao = r.id_acao ";

    public List<RegistroAcao> listarPorUsuario(long idUsuario) throws SQLException {
        List<RegistroAcao> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE r.id_usuario = ? ORDER BY r.id_registro")) {
            ps.setLong(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        }
        return lista;
    }

    public RegistroAcao buscarPorId(long id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE r.id_registro = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    /** Insere e devolve o id gerado pelo IDENTITY do Oracle. A data fica com o DEFAULT SYSDATE. */
    public long inserir(RegistroAcao registro) throws SQLException {
        String sql = "INSERT INTO SOULUP_REGISTRO_ACAO (id_usuario, id_acao) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_registro"})) {
            ps.setLong(1, registro.getIdUsuario());
            ps.setLong(2, registro.getIdAcao());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getLong(1);
            }
        }
    }

    public boolean excluir(long id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM SOULUP_REGISTRO_ACAO WHERE id_registro = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Regra de negocio: a mesma acao nao pode ser registrada duas vezes pelo mesmo usuario. */
    public boolean existe(long idUsuario, long idAcao) throws SQLException {
        String sql = "SELECT 1 FROM SOULUP_REGISTRO_ACAO WHERE id_usuario = ? AND id_acao = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUsuario);
            ps.setLong(2, idAcao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Conta quantos registros usam o usuario (usado antes de excluir o usuario). */
    public int contarPorUsuario(long idUsuario) throws SQLException {
        return contar("SELECT COUNT(*) FROM SOULUP_REGISTRO_ACAO WHERE id_usuario = ?", idUsuario);
    }

    /** Conta quantos registros usam a acao (usado antes de excluir a acao). */
    public int contarPorAcao(long idAcao) throws SQLException {
        return contar("SELECT COUNT(*) FROM SOULUP_REGISTRO_ACAO WHERE id_acao = ?", idAcao);
    }

    private int contar(String sql, long id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private RegistroAcao montar(ResultSet rs) throws SQLException {
        return new RegistroAcao(
                rs.getLong("id_registro"),
                rs.getLong("id_usuario"),
                rs.getLong("id_acao"),
                rs.getString("nome_acao"),
                rs.getInt("pontos"),
                rs.getDate("dt_registro").toLocalDate());
    }
}
