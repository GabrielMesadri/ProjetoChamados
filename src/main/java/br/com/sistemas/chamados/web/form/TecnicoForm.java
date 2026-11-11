package br.com.sistemas.chamados.web.form;

import br.com.sistemas.chamados.dto.TecnicoResponse;
import br.com.sistemas.chamados.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TecnicoForm {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotNull(message = "Perfil é obrigatório")
    private Perfil perfil;

    public static TecnicoForm de(TecnicoResponse t) {
        TecnicoForm form = new TecnicoForm();
        form.id = t.id();
        form.nome = t.nome();
        form.email = t.email();
        form.perfil = t.perfil();
        return form;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public String getNome(){return nome;}
    public void setNome(String nome){this.nome = nome;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}
    public Perfil getPerfil(){return perfil;}
    public void setPerfil(Perfil perfil){this.perfil = perfil;}
}
