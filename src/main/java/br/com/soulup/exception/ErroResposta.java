package br.com.soulup.exception;

/**
 * Formato padrao de erro devolvido pela API:
 * { "status": 404, "mensagem": "Usuário não encontrado" }
 */
public class ErroResposta {

    private final int status;
    private final String mensagem;

    public ErroResposta(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public int getStatus() { return status; }
    public String getMensagem() { return mensagem; }
}
