package br.com.sistemas.chamados.web.form;

import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChamadoForm {

    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 150, message = "Título deve ter no máximo 150 caracteres")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    @NotNull(message = "Prioridade é obrigatória")
    private Prioridade prioridade;

    private StatusChamado status;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoriaId;

    private Long tecnicoId;

    public static ChamadoForm de(ChamadoResponse c) {
        ChamadoForm form = new ChamadoForm();
        form.id = c.id();
        form.titulo = c.titulo();
        form.descricao = c.descricao();
        form.prioridade = c.prioridade();
        form.status = c.status();
        form.clienteId = c.clienteId();
        form.categoriaId = c.categoriaId();
        form.tecnicoId = c.tecnicoId();
        return form;
    }

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public String getTitulo(){return titulo;}
    public void setTitulo(String titulo){this.titulo = titulo;}
    public String getDescricao(){return descricao;}
    public void setDescricao(String descricao){this.descricao = descricao;}
    public Prioridade getPrioridade(){return prioridade;}
    public void setPrioridade(Prioridade prioridade){this.prioridade = prioridade;}
    public StatusChamado getStatus(){return status;}
    public void setStatus(StatusChamado status){this.status = status;}
    public Long getClienteId(){return clienteId;}
    public void setClienteId(Long clienteId){this.clienteId = clienteId;}
    public Long getCategoriaId(){return categoriaId;}
    public void setCategoriaId(Long categoriaId){this.categoriaId = categoriaId;}
    public Long getTecnicoId(){return tecnicoId;}
    public void setTecnicoId(Long tecnicoId){this.tecnicoId = tecnicoId;}
}
