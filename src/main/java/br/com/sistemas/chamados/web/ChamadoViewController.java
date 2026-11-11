package br.com.sistemas.chamados.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;
import br.com.sistemas.chamados.service.ComentarioService;
import br.com.sistemas.chamados.service.TecnicoService;
import br.com.sistemas.chamados.web.form.ChamadoForm;
import br.com.sistemas.chamados.web.form.ComentarioForm;

@Controller
@RequestMapping("/web/chamados")
public class ChamadoViewController {

    private final ChamadoService chamadoService;
    private final ClienteService clienteService;
    private final CategoriaService categoriaService;
    private final TecnicoService tecnicoService;
    private final ComentarioService comentarioService;

    public ChamadoViewController(ChamadoService chamadoService, ClienteService clienteService,
            CategoriaService categoriaService, TecnicoService tecnicoService, ComentarioService comentarioService) {
        this.chamadoService = chamadoService;
        this.clienteService = clienteService;
        this.categoriaService = categoriaService;
        this.tecnicoService = tecnicoService;
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) StatusChamado status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<ChamadoResponse> chamados = chamadoService.listar(status, prioridade, clienteId, pageable);

        model.addAttribute("chamados", chamados);
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("statusValues", StatusChamado.values());
        model.addAttribute("prioridadeValues", Prioridade.values());
        model.addAttribute("statusFiltro", status);
        model.addAttribute("prioridadeFiltro", prioridade);
        model.addAttribute("clienteIdFiltro", clienteId);
        return "chamados/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("chamadoForm", new ChamadoForm());
        carregarListasAuxiliares(model);
        return "chamados/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("chamadoForm", ChamadoForm.de(chamadoService.buscar(id)));
        carregarListasAuxiliares(model);
        return "chamados/form";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("chamado", chamadoService.buscar(id));
        model.addAttribute("comentarios", comentarioService.listar(id));
        model.addAttribute("comentarioForm", new ComentarioForm());
        model.addAttribute("tecnicos", tecnicoService.listar());
        model.addAttribute("statusValues", StatusChamado.values());
        return "chamados/detalhe";
    }

    @PostMapping
    public String criar(@Validated @ModelAttribute("chamadoForm") ChamadoForm form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            carregarListasAuxiliares(model);
            return "chamados/form";
        }
        try {
            ChamadoResponse criado = chamadoService.criar(new ChamadoRequest(
                    form.getTitulo(), form.getDescricao(), form.getPrioridade(), null,
                    form.getClienteId(), form.getCategoriaId(), form.getTecnicoId()));
            redirect.addFlashAttribute("mensagem", "Chamado aberto com sucesso");
            return "redirect:/web/chamados/" + criado.id();
        } catch (RecursoNaoEncontradoException e) {
            model.addAttribute("erro", e.getMessage());
            carregarListasAuxiliares(model);
            return "chamados/form";
        }
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Validated @ModelAttribute("chamadoForm") ChamadoForm form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            carregarListasAuxiliares(model);
            return "chamados/form";
        }
        try {
            chamadoService.atualizar(id, new ChamadoRequest(
                    form.getTitulo(), form.getDescricao(), form.getPrioridade(), form.getStatus(),
                    form.getClienteId(), form.getCategoriaId(), form.getTecnicoId()));
        } catch (ChamadoFechadoException | RecursoNaoEncontradoException e) {
            model.addAttribute("erro", e.getMessage());
            carregarListasAuxiliares(model);
            return "chamados/form";
        }
        redirect.addFlashAttribute("mensagem", "Chamado atualizado com sucesso");
        return "redirect:/web/chamados/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        chamadoService.excluir(id);
        redirect.addFlashAttribute("mensagem", "Chamado excluído com sucesso");
        return "redirect:/web/chamados";
    }

    @PostMapping("/{id}/atribuir")
    public String atribuir(@PathVariable Long id, @RequestParam Long tecnicoId, RedirectAttributes redirect) {
        try {
            chamadoService.atribuirTecnico(id, tecnicoId);
            redirect.addFlashAttribute("mensagem", "Técnico atribuído com sucesso");
        } catch (ChamadoFechadoException | RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/web/chamados/" + id;
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(@PathVariable Long id, @RequestParam StatusChamado status, RedirectAttributes redirect) {
        try {
            chamadoService.alterarStatus(id, status);
            redirect.addFlashAttribute("mensagem", "Status atualizado com sucesso");
        } catch (ChamadoFechadoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/web/chamados/" + id;
    }

    @PostMapping("/{id}/comentarios")
    public String comentar(@PathVariable Long id, @Validated @ModelAttribute("comentarioForm") ComentarioForm form,
            BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("erro", "Texto e autor do comentário são obrigatórios");
            return "redirect:/web/chamados/" + id;
        }
        comentarioService.criar(id, new ComentarioRequest(form.getTexto(), form.getAutor()));
        return "redirect:/web/chamados/" + id;
    }

    private void carregarListasAuxiliares(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("tecnicos", tecnicoService.listar());
        model.addAttribute("prioridadeValues", Prioridade.values());
        model.addAttribute("statusValues", StatusChamado.values());
    }
}
