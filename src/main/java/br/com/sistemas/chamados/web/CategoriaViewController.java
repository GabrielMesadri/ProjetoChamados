package br.com.sistemas.chamados.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.web.form.CategoriaForm;

@Controller
@RequestMapping("/web/categorias")
public class CategoriaViewController {

    private final CategoriaService service;

    public CategoriaViewController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.listar());
        return "categorias/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("categoriaForm", new CategoriaForm());
        return "categorias/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("categoriaForm", CategoriaForm.de(service.buscar(id)));
        return "categorias/form";
    }

    @PostMapping
    public String criar(@Validated @ModelAttribute("categoriaForm") CategoriaForm form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "categorias/form";
        }
        try {
            service.criar(new CategoriaRequest(form.getNome(), form.getDescricao()));
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return "categorias/form";
        }
        redirect.addFlashAttribute("mensagem", "Categoria criada com sucesso");
        return "redirect:/web/categorias";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Validated @ModelAttribute("categoriaForm") CategoriaForm form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "categorias/form";
        }
        try {
            service.atualizar(id, new CategoriaRequest(form.getNome(), form.getDescricao()));
        } catch (RegraNegocioException | RecursoNaoEncontradoException e) {
            model.addAttribute("erro", e.getMessage());
            return "categorias/form";
        }
        redirect.addFlashAttribute("mensagem", "Categoria atualizada com sucesso");
        return "redirect:/web/categorias";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("mensagem", "Categoria excluída com sucesso");
        return "redirect:/web/categorias";
    }
}
