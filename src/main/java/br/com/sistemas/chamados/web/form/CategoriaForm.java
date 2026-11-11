package br.com.sistemas.chamados.web.form;

import br.com.sistemas.chamados.dto.CategoriaResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaForm {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricao;

    public static CategoriaForm de(CategoriaResponse c) {
        CategoriaForm form = new CategoriaForm();
        form.id = c.id();
        form.nome = c.nome();
        form.descricao = c.descricao();
        return form;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public String getNome(){return nome;}
    public void setNome(String nome){this.nome = nome;}
    public String getDescricao(){return descricao;}
    public void setDescricao(String descricao){this.descricao = descricao;}
}
