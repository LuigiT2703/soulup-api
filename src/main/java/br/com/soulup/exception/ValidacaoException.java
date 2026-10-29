package br.com.soulup.exception;

/** Dados enviados pelo cliente sao invalidos. Vira HTTP 400. */
public class ValidacaoException extends RuntimeException {
    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
