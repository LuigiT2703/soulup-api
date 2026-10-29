package br.com.soulup.model;

/**
 * Acao sustentavel do catalogo (tabela SOULUP_ACAO).
 */
public class Acao {

    private Long id;
    private String nome;
    // Integer (e nao int) para conseguir saber se o campo veio vazio no JSON
    private Integer pontos;
    private String categoria;

    public Acao() {
    }

    public Acao(Long id, String nome, Integer pontos, String categoria) {
        this.id = id;
        this.nome = nome;
        this.pontos = pontos;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getPontos() { return pontos; }
    public void setPontos(Integer pontos) { this.pontos = pontos; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
