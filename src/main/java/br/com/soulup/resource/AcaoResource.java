package br.com.soulup.resource;

import br.com.soulup.bo.AcaoBO;
import br.com.soulup.model.Acao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Endpoints do catalogo de acoes sustentaveis: /acoes
 */
@RestController
@RequestMapping("/acoes")
public class AcaoResource {

    private final AcaoBO acaoBO = new AcaoBO();

    /** GET /acoes ou GET /acoes?categoria=MOBILIDADE */
    @GetMapping
    public List<Acao> listar(@RequestParam(required = false) String categoria) throws SQLException {
        return acaoBO.listar(categoria);
    }

    @GetMapping("/{id}")
    public Acao buscar(@PathVariable long id) throws SQLException {
        return acaoBO.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Acao> cadastrar(@RequestBody Acao acao) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(acaoBO.cadastrar(acao));
    }

    @PutMapping("/{id}")
    public Acao atualizar(@PathVariable long id, @RequestBody Acao acao) throws SQLException {
        return acaoBO.atualizar(id, acao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable long id) throws SQLException {
        acaoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
