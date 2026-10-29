package br.com.soulup.exception;

/** Recurso (usuario, acao ou registro) nao existe. Vira HTTP 404. */
public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
