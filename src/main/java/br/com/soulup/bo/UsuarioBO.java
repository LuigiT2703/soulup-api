package br.com.soulup.bo;

import br.com.soulup.dao.RegistroAcaoDAO;
import br.com.soulup.dao.UsuarioDAO;
import br.com.soulup.exception.ConflitoException;
import br.com.soulup.exception.NaoEncontradoException;
import br.com.soulup.exception.ValidacaoException;
import br.com.soulup.model.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 * Regras de negocio e validacoes de Usuario.
 * O Resource chama o BO, e o BO chama o DAO (nunca o Resource direto no DAO).
 */
public class UsuarioBO {

    // Letras (com acento) e espacos, de 3 a 80 caracteres
    private static final String REGEX_NOME = "^[\\p{L} ]{3,80}$";
    // Formato simples de e-mail: algo@dominio.ext
    private static final String REGEX_EMAIL = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RegistroAcaoDAO registroDAO = new RegistroAcaoDAO();

    public List<Usuario> listar() throws SQLException {
        List<Usuario> usuarios = usuarioDAO.listar();
        usuarios.forEach(u -> u.setNivel(calcularNivel(u.getPontos())));
        return usuarios;
    }

    public List<Usuario> ranking() throws SQLException {
        List<Usuario> usuarios = usuarioDAO.listarRanking();
        usuarios.forEach(u -> u.setNivel(calcularNivel(u.getPontos())));
        return usuarios;
    }

    public Usuario buscarPorId(long id) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }
        usuario.setNivel(calcularNivel(usuario.getPontos()));
        return usuario;
    }

    public Usuario cadastrar(Usuario usuario) throws SQLException {
        validar(usuario);
        verificarDuplicado(usuario, 0);
        long id = usuarioDAO.inserir(usuario);
        return buscarPorId(id);
    }

    public Usuario atualizar(long id, Usuario usuario) throws SQLException {
        buscarPorId(id); // 404 se nao existir
        validar(usuario);
        verificarDuplicado(usuario, id);
        usuarioDAO.atualizar(id, usuario);
        return buscarPorId(id);
    }

    public void excluir(long id) throws SQLException {
        buscarPorId(id); // 404 se nao existir
        if (registroDAO.contarPorUsuario(id) > 0) {
            throw new ConflitoException("Usuário possui ações registradas e não pode ser excluído");
        }
        usuarioDAO.excluir(id);
    }

    /**
     * Nivel do usuario conforme a pontuacao total.
     */
    public static String calcularNivel(int pontos) {
        if (pontos >= 80) return "Guardião da Terra";
        if (pontos >= 60) return "Eco Herói";
        if (pontos >= 40) return "Consciência Verde";
        if (pontos >= 20) return "Aprendiz Sustentável";
        return "Iniciante";
    }

    /** Valida nome e e-mail e ja deixa os textos limpos (sem espacos sobrando). */
    private void validar(Usuario usuario) {
        if (usuario == null) {
            throw new ValidacaoException("Corpo da requisição é obrigatório");
        }
        String nome = usuario.getNome() == null ? "" : usuario.getNome().trim().replaceAll("\\s+", " ");
        String email = usuario.getEmail() == null ? "" : usuario.getEmail().trim().toLowerCase();

        if (!nome.matches(REGEX_NOME)) {
            throw new ValidacaoException("Nome deve ter de 3 a 80 caracteres, apenas letras e espaços");
        }
        if (email.length() > 120 || !email.matches(REGEX_EMAIL)) {
            throw new ValidacaoException("E-mail inválido (formato nome@dominio.com, até 120 caracteres)");
        }
        usuario.setNome(nome);
        usuario.setEmail(email);
    }

    private void verificarDuplicado(Usuario usuario, long idAtual) throws SQLException {
        if (usuarioDAO.existeNome(usuario.getNome(), idAtual)) {
            throw new ConflitoException("Já existe um usuário com esse nome");
        }
        if (usuarioDAO.existeEmail(usuario.getEmail(), idAtual)) {
            throw new ConflitoException("Já existe um usuário com esse e-mail");
        }
    }
}
