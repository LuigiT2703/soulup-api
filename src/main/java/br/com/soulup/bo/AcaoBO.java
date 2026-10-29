package br.com.soulup.bo;

import br.com.soulup.dao.AcaoDAO;
import br.com.soulup.dao.RegistroAcaoDAO;
import br.com.soulup.exception.ConflitoException;
import br.com.soulup.exception.NaoEncontradoException;
import br.com.soulup.exception.ValidacaoException;
import br.com.soulup.model.Acao;

import java.sql.SQLException;
import java.util.List;

/**
 * Regras de negocio e validacoes das Acoes sustentaveis.
 */
public class AcaoBO {

    /** Mesmas categorias do CHECK da tabela SOULUP_ACAO. */
    public static final List<String> CATEGORIAS = List.of(
            "MOBILIDADE", "RESIDUOS", "ENERGIA", "AGUA", "CONSUMO", "NATUREZA", "ENGAJAMENTO");

    private final AcaoDAO acaoDAO = new AcaoDAO();
    private final RegistroAcaoDAO registroDAO = new RegistroAcaoDAO();

    /** Lista todas ou filtra por categoria (?categoria=MOBILIDADE). */
    public List<Acao> listar(String categoria) throws SQLException {
        if (categoria == null || categoria.isBlank()) {
            return acaoDAO.listar();
        }
        return acaoDAO.listarPorCategoria(validarCategoria(categoria));
    }

    public Acao buscarPorId(long id) throws SQLException {
        Acao acao = acaoDAO.buscarPorId(id);
        if (acao == null) {
            throw new NaoEncontradoException("Ação não encontrada");
        }
        return acao;
    }

    public Acao cadastrar(Acao acao) throws SQLException {
        validar(acao);
        if (acaoDAO.existeNome(acao.getNome(), 0)) {
            throw new ConflitoException("Já existe uma ação com esse nome");
        }
        long id = acaoDAO.inserir(acao);
        return buscarPorId(id);
    }

    public Acao atualizar(long id, Acao acao) throws SQLException {
        buscarPorId(id); // 404 se nao existir
        validar(acao);
        if (acaoDAO.existeNome(acao.getNome(), id)) {
            throw new ConflitoException("Já existe uma ação com esse nome");
        }
        acaoDAO.atualizar(id, acao);
        return buscarPorId(id);
    }

    public void excluir(long id) throws SQLException {
        buscarPorId(id); // 404 se nao existir
        if (registroDAO.contarPorAcao(id) > 0) {
            throw new ConflitoException("Ação já foi registrada por usuários e não pode ser excluída");
        }
        acaoDAO.excluir(id);
    }

    private void validar(Acao acao) {
        if (acao == null) {
            throw new ValidacaoException("Corpo da requisição é obrigatório");
        }
        String nome = acao.getNome() == null ? "" : acao.getNome().trim().replaceAll("\\s+", " ");
        if (nome.length() < 3 || nome.length() > 60) {
            throw new ValidacaoException("Nome da ação deve ter de 3 a 60 caracteres");
        }
        if (acao.getPontos() == null || acao.getPontos() < 1 || acao.getPontos() > 100) {
            throw new ValidacaoException("Pontos devem estar entre 1 e 100");
        }
        acao.setNome(nome);
        acao.setCategoria(validarCategoria(acao.getCategoria()));
    }

    /** Aceita minusculas (ex.: "agua") e devolve sempre em maiusculas. */
    private String validarCategoria(String categoria) {
        String valor = categoria == null ? "" : categoria.trim().toUpperCase();
        if (!CATEGORIAS.contains(valor)) {
            throw new ValidacaoException("Categoria inválida. Use: " + String.join(", ", CATEGORIAS));
        }
        return valor;
    }
}
