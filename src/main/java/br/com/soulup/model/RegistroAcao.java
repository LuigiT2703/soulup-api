package br.com.soulup.model;

import java.time.LocalDate;

/**
 * Acao realizada por um usuario (tabela SOULUP_REGISTRO_ACAO).
 * nomeAcao e pontos vem do JOIN com SOULUP_ACAO, so para exibir no front.
 */
public class RegistroAcao {

    private Long id;
    private Long idUsuario;
    private Long idAcao;
    private String nomeAcao;
    private int pontos;
    private LocalDate dataRegistro;

    public RegistroAcao() {
    }

    public RegistroAcao(Long id, Long idUsuario, Long idAcao, String nomeAcao, int pontos, LocalDate dataRegistro) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idAcao = idAcao;
        this.nomeAcao = nomeAcao;
        this.pontos = pontos;
        this.dataRegistro = dataRegistro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getIdAcao() { return idAcao; }
    public void setIdAcao(Long idAcao) { this.idAcao = idAcao; }

    public String getNomeAcao() { return nomeAcao; }
    public void setNomeAcao(String nomeAcao) { this.nomeAcao = nomeAcao; }

    public int getPontos() { return pontos; }
    public void setPontos(int pontos) { this.pontos = pontos; }

    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
}
