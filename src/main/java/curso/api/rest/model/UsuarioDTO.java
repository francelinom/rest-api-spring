package curso.api.rest.model;

import javax.persistence.Column;
import java.io.Serializable;

public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String login;
    private String senha;
    private String nome;
    private String cpf;
    private String cep;

    private String numero;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.login = usuario.getLogin();
        this.senha = usuario.getSenha();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.cep = usuario.getCep();
    }

    public UsuarioDTO(Telefone telefone) {
        this.numero = telefone.getNumero();
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
