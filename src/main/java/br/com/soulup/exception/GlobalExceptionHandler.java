package br.com.soulup.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;

/**
 * Tratamento centralizado de erros: qualquer excecao lancada nos Resources
 * cai aqui e vira um JSON { status, mensagem } com o codigo HTTP certo.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(NaoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroResposta> validacao(ValidacaoException e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> conflito(ConflitoException e) {
        return responder(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Erros vindos do Oracle. As validacoes do BO ja evitam a maioria,
     * mas as constraints do banco sao a ultima barreira:
     * ORA-00001 (UNIQUE) -> 409, ORA-02291 (FK pai nao existe) -> 404,
     * ORA-02292 (existem filhos) -> 409, ORA-02290 (CHECK) -> 400.
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErroResposta> erroBanco(SQLException e) {
        switch (e.getErrorCode()) {
            case 1:
                return responder(HttpStatus.CONFLICT, "Registro duplicado: já existe um cadastro com esses dados");
            case 2291:
                return responder(HttpStatus.NOT_FOUND, "Usuário ou ação informada não existe");
            case 2292:
                return responder(HttpStatus.CONFLICT, "Não é possível excluir: existem registros vinculados");
            case 2290:
                return responder(HttpStatus.BAD_REQUEST, "Dados inválidos para o banco (regra CHECK)");
            default:
                log.error("Erro no banco de dados", e);
                return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados");
        }
    }

    /** JSON mal formado ou com tipo errado (ex.: pontos = "abc"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> jsonInvalido(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou mal formatado");
    }

    /** Parametro da URL com tipo errado (ex.: /usuarios/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return responder(HttpStatus.BAD_REQUEST, "Parâmetro inválido: " + e.getName());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> rotaInexistente(NoResourceFoundException e) {
        return responder(HttpStatus.NOT_FOUND, "Rota não encontrada");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResposta> metodoNaoSuportado(HttpRequestMethodNotSupportedException e) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP não permitido nesta rota");
    }

    /** Qualquer outro erro inesperado (ex.: banco fora do ar, variavel de ambiente faltando). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> erroGeral(Exception e) {
        log.error("Erro inesperado", e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor");
    }

    private ResponseEntity<ErroResposta> responder(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ErroResposta(status.value(), mensagem));
    }
}
