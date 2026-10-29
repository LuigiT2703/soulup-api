package br.com.soulup.resource;

import br.com.soulup.bo.RegistroAcaoBO;
import br.com.soulup.bo.UsuarioBO;
import br.com.soulup.model.RegistroAcao;
import br.com.soulup.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Endpoints de usuarios: /usuarios
 * Os erros (404, 400, 409, 500) sao tratados no GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

    private final UsuarioBO usuarioBO = new UsuarioBO();
    private final RegistroAcaoBO registroBO = new RegistroAcaoBO();

    @GetMapping
    public List<Usuario> listar() throws SQLException {
        return usuarioBO.listar();
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable long id) throws SQLException {
        return usuarioBO.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioBO.cadastrar(usuario));
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable long id, @RequestBody Usuario usuario) throws SQLException {
        return usuarioBO.atualizar(id, usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable long id) throws SQLException {
        usuarioBO.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /** Historico de acoes registradas pelo usuario. */
    @GetMapping("/{id}/registros")
    public List<RegistroAcao> registros(@PathVariable long id) throws SQLException {
        return registroBO.listarPorUsuario(id);
    }
}
