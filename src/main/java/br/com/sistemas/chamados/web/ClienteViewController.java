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

import br.com.sistemas.chamados.dto.ClienteRequest;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;
import br.com.sistemas.chamados.web.form.ClienteForm;

@Controller
@RequestMapping("/web/clientes")
public class ClienteViewController {

    private final ClienteService service;
    private final ChamadoService chamadoService;

    public ClienteViewController(ClienteService service, ChamadoService chamadoService) {
        this.service = service;
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listar());
        return "clientes/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("clienteForm", new ClienteForm());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("clienteForm", ClienteForm.de(service.buscar(id)));
        return "clientes/form";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", service.buscar(id));
        model.addAttribute("chamados", chamadoService.listarPorCliente(id));
        return "clientes/detalhe";
    }

    @PostMapping
    public String criar(@Validated @ModelAttribute("clienteForm") ClienteForm form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "clientes/form";
        }
        try {
            service.criar(new ClienteRequest(form.getNome(), form.getEmail(), form.getTelefone()));
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            return "clientes/form";
        }
        redirect.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso");
        return "redirect:/web/clientes";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Validated @ModelAttribute("clienteForm") ClienteForm form,
            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "clientes/form";
        }
        try {
            service.atualizar(id, new ClienteRequest(form.getNome(), form.getEmail(), form.getTelefone()));
        } catch (RegraNegocioException | RecursoNaoEncontradoException e) {
            model.addAttribute("erro", e.getMessage());
            return "clientes/form";
        }
        redirect.addFlashAttribute("mensagem", "Cliente atualizado com sucesso");
        return "redirect:/web/clientes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("mensagem", "Cliente excluído com sucesso");
        return "redirect:/web/clientes";
    }
}
