package br.com.fiap.model;

import java.util.UUID;

public class Feedback {
    
    private String id;
    private String descricao;
    private int nota;
    private String data;

    // Construtor padrão
    public Feedback() {
        // Gera um ID único automaticamente assim que o objeto é criado
        this.id = UUID.randomUUID().toString();
    }

    // Getters e Setters 
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    
    @Override
    public String toString() {
        return "Feedback [id=" + id + ", nota=" + nota + "]";
    }
}