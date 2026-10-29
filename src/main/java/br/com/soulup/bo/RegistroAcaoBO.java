package br.com.soulup.bo;

import br.com.soulup.dao.AcaoDAO;
import br.com.soulup.dao.RegistroAcaoDAO;
import br.com.soulup.dao.UsuarioDAO;
import br.com.soulup.exception.ConflitoException;
import br.com.soulup.exception.NaoEncontradoException;
import br.com.soulup.exception.ValidacaoException;
import br.com.soulup.model.RegistroAcao;

import java.sql.SQLException;
import java.util.List;

/**
 * Regras de negocio do registro de acoes feitas pelos usuarios.
 */
public class RegistroAcaoBO {

    private final RegistroAcaoDAO registroDAO = new RegistroAcaoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final AcaoDAO acaoDAO = new AcaoDAO();

    public List<RegistroAcao> listarPorUsuario(long idUsuario) throws SQLException {
        if (usuarioDAO.buscarPorId(idUsuario) == null) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }
        return registroDAO.listarPorUsuario(idUsuario);
    }

    public RegistroAcao registrar(RegistroAcao registro) throws SQLException {
        if (registro == null || registro.getIdUsuario() == null || registro.getIdAcao() == null) {
            throw new ValidacaoException("idUsuario e idAcao são obrigatórios");
        }
        if (registro.getIdUsuario() <= 0 || registro.getIdAcao() <= 0) {
            throw new ValidacaoException("idUsuario e idAcao devem ser números positivos");
        }
        if (usuarioDAO.buscarPorId(registro.getIdUsuario()) == null) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }
        if (acaoDAO.buscarPorId(registro.getIdAcao()) == null) {
            throw new NaoEncontradoException("Ação não encontrada");
        }
        // Regra principal: a mesma acao nao pode ser registrada duas vezes pelo mesmo usuario
        if (registroDAO.existe(registro.getIdUsuario(), registro.getIdAcao())) {
            throw new ConflitoException("Essa ação já foi registrada por este usuário");
        }
        long id = registroDAO.inserir(registro);
        return registroDAO.buscarPorId(id);
    }

    public void excluir(long id) throws SQLException {
        if (!registroDAO.excluir(id)) {
            throw new NaoEncontradoException("Registro não encontrado");
        }
    }
}
