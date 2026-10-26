package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.dto.CategoriaResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoriaService service;

    @Test
    void deveCriarCategoriaComSucesso() throws Exception {
        CategoriaRequest request = new CategoriaRequest("Hardware", "Problemas de hardware");
        CategoriaResponse response = new CategoriaResponse(1L, "Hardware", "Problemas de hardware");
        given(service.criar(any(CategoriaRequest.class))).willReturn(response);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Hardware"));
    }

    @Test
    void deveRetornar400QuandoNomeEstiverEmBranco() throws Exception {
        CategoriaRequest request = new CategoriaRequest("", "Descrição qualquer");

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoNomeDuplicado() throws Exception {
        CategoriaRequest request = new CategoriaRequest("Hardware", "Problemas de hardware");
        given(service.criar(any(CategoriaRequest.class)))
                .willThrow(new RegraNegocioException("Já existe categoria com o nome Hardware"));

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar404QuandoCategoriaNaoExiste() throws Exception {
        given(service.buscar(eq(99L))).willThrow(new RecursoNaoEncontradoException("Categoria 99 não encontrada"));

        mockMvc.perform(get("/categorias/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar204AoExcluir() throws Exception {
        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent());
    }
}
