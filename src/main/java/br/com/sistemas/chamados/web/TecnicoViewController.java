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

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.TecnicoService;
import br.com.sistemas.chamados.web.form.TecnicoForm;

@Controller
@RequestMapping("/web/tecnicos")
public class TecnicoViewController {

    private final TecnicoService service;

    public TecnicoViewController(TecnicoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tecnicos", service.listar());
        return "tecnicos/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("tecnicoForm", new TecnicoForm());
        model.addAttribute("perfis", Perfil.values());
        return "tecnicos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("tecnicoForm", TecnicoForm.de(service.buscar(id)));
        model.addAttribute("perfis", Perfil.values());
        return "tecnicos/form";
    }

    @PostMapping
    public String criar(@Validated @ModelAttribute("tecnicoForm") TecnicoForm form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            return "tecnicos/form";
        }
        try {
            service.criar(new TecnicoRequest(form.getNome(), form.getEmail(), form.getPerfil()));
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("perfis", Perfil.values());
            return "tecnicos/form";
        }
        redirect.addFlashAttribute("mensagem", "Técnico cadastrado com sucesso");
        return "redirect:/web/tecnicos";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Validated @ModelAttribute("tecnicoForm") TecnicoForm form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            return "tecnicos/form";
        }
        try {
            service.atualizar(id, new TecnicoRequest(form.getNome(), form.getEmail(), form.getPerfil()));
        } catch (RegraNegocioException | RecursoNaoEncontradoException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("perfis", Perfil.values());
            return "tecnicos/form";
        }
        redirect.addFlashAttribute("mensagem", "Técnico atualizado com sucesso");
        return "redirect:/web/tecnicos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("mensagem", "Técnico excluído com sucesso");
        return "redirect:/web/tecnicos";
    }
}
