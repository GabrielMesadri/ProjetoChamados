package br.com.sistemas.chamados.web.form;

import br.com.sistemas.chamados.dto.ClienteResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClienteForm {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    public static ClienteForm de(ClienteResponse c) {
        ClienteForm form = new ClienteForm();
        form.id = c.id();
        form.nome = c.nome();
        form.email = c.email();
        form.telefone = c.telefone();
        return form;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public String getNome(){return nome;}
    public void setNome(String nome){this.nome = nome;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}
    public String getTelefone(){return telefone;}
    public void setTelefone(String telefone){this.telefone = telefone;}
}
