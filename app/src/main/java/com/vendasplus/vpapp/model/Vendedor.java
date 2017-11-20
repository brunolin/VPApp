package com.vendasplus.vpapp.model;

/**
 * Created by Brunolin on 07/06/2017.
 */

public class Vendedor {

    public int idVendedor, pontos;
    public Long telefone;
    public String nome, email, cidade, estado, senha, cpf;

    public Vendedor() {

    }
    public Vendedor(int id, String nome, Long telefone, String cpf,  int pontos, String cidade, String estado, String email, String senha){
        this.idVendedor = id;
        this.nome = nome;
        this.cpf = cpf;
        this.pontos = pontos;
        this.telefone = telefone;
        this.cidade = cidade;
        this.estado = estado;
        this.email = email;
        this.senha = senha;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public Long getTelefone() {
        return telefone;
    }

    public void setTelefone(Long telefone) {
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
