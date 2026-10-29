package br.com.soulup.resource;

import br.com.soulup.bo.UsuarioBO;
import br.com.soulup.model.Usuario;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

/**
 * Ranking da gamificacao: usuarios ordenados pela pontuacao (maior primeiro).
 */
@RestController
@RequestMapping("/ranking")
public class RankingResource {

    private final UsuarioBO usuarioBO = new UsuarioBO();

    @GetMapping
    public List<Usuario> ranking() throws SQLException {
        return usuarioBO.ranking();
    }
}
