package br.com.sistemas.chamados.web;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;
import br.com.sistemas.chamados.service.TecnicoService;

@Controller
public class HomeController {

    private final ChamadoService chamadoService;
    private final ClienteService clienteService;
    private final CategoriaService categoriaService;
    private final TecnicoService tecnicoService;

    public HomeController(ChamadoService chamadoService, ClienteService clienteService,
            CategoriaService categoriaService, TecnicoService tecnicoService) {
        this.chamadoService = chamadoService;
        this.clienteService = clienteService;
        this.categoriaService = categoriaService;
        this.tecnicoService = tecnicoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalChamados", chamadoService.listar(null, null, null, Pageable.unpaged()).getTotalElements());
        model.addAttribute("totalClientes", clienteService.listar().size());
        model.addAttribute("totalCategorias", categoriaService.listar().size());
        model.addAttribute("totalTecnicos", tecnicoService.listar().size());
        return "index";
    }
}
