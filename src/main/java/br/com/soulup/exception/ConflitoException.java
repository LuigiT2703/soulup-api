package br.com.soulup.exception;

/** Operacao conflita com dados existentes (duplicado ou exclusao com vinculos). Vira HTTP 409. */
public class ConflitoException extends RuntimeException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
