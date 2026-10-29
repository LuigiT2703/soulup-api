package br.com.soulup.resource;

import br.com.soulup.bo.RegistroAcaoBO;
import br.com.soulup.model.RegistroAcao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * Endpoints para registrar (e desfazer) uma acao feita por um usuario: /registros
 * Para listar os registros de um usuario use GET /usuarios/{id}/registros.
 */
@RestController
@RequestMapping("/registros")
public class RegistroResource {

    private final RegistroAcaoBO registroBO = new RegistroAcaoBO();

    /** Corpo: { "idUsuario": 1, "idAcao": 3 } */
    @PostMapping
    public ResponseEntity<RegistroAcao> registrar(@RequestBody RegistroAcao registro) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroBO.registrar(registro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable long id) throws SQLException {
        registroBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
