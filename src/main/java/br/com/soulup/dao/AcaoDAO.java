package br.com.soulup.dao;

import br.com.soulup.model.Acao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a tabela SOULUP_ACAO (catalogo de acoes sustentaveis).
 */
public class AcaoDAO {

    private static final String SELECT = "SELECT id_acao, nome, pontos, categoria FROM SOULUP_ACAO ";

    public List<Acao> listar() throws SQLException {
        List<Acao> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "ORDER BY id_acao");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public List<Acao> listarPorCategoria(String categoria) throws SQLException {
        List<Acao> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE categoria = ? ORDER BY id_acao")) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        }
        return lista;
    }

    public Acao buscarPorId(long id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE id_acao = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    /** Insere e devolve o id gerado pelo IDENTITY do Oracle. */
    public long inserir(Acao acao) throws SQLException {
        String sql = "INSERT INTO SOULUP_ACAO (nome, pontos, categoria) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id_acao"})) {
            ps.setString(1, acao.getNome());
            ps.setInt(2, acao.getPontos());
            ps.setString(3, acao.getCategoria());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getLong(1);
            }
        }
    }

    public boolean atualizar(long id, Acao acao) throws SQLException {
        String sql = "UPDATE SOULUP_ACAO SET nome = ?, pontos = ?, categoria = ? WHERE id_acao = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acao.getNome());
            ps.setInt(2, acao.getPontos());
            ps.setString(3, acao.getCategoria());
            ps.setLong(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(long id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM SOULUP_ACAO WHERE id_acao = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Verifica se ja existe OUTRA acao com o mesmo nome (ignorando o id informado). */
    public boolean existeNome(String nome, long idIgnorar) throws SQLException {
        String sql = "SELECT 1 FROM SOULUP_ACAO WHERE UPPER(nome) = UPPER(?) AND id_acao <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setLong(2, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Acao montar(ResultSet rs) throws SQLException {
        return new Acao(
                rs.getLong("id_acao"),
                rs.getString("nome"),
                rs.getInt("pontos"),
                rs.getString("categoria"));
    }
}
