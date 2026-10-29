package br.com.soulup.model;

import java.time.LocalDate;

/**
 * Usuario participante da gamificacao (tabela SOULUP_USUARIO).
 * totalAcoes, pontos e nivel NAO ficam salvos no banco: sao calculados
 * a partir das acoes registradas (LEFT JOIN + SUM).
 */
public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private LocalDate dataCadastro;
    private int totalAcoes;
    private int pontos;
    private String nivel;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, LocalDate dataCadastro, int totalAcoes, int pontos) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataCadastro = dataCadastro;
        this.totalAcoes = totalAcoes;
        this.pontos = pontos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public int getTotalAcoes() { return totalAcoes; }
    public void setTotalAcoes(int totalAcoes) { this.totalAcoes = totalAcoes; }

    public int getPontos() { return pontos; }
    public void setPontos(int pontos) { this.pontos = pontos; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
}
