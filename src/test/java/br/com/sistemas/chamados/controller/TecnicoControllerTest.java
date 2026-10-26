package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.dto.TecnicoResponse;
import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.TecnicoService;

@WebMvcTest(TecnicoController.class)
class TecnicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TecnicoService service;

    @Test
    void deveCriarTecnicoComSucesso() throws Exception {
        TecnicoRequest request = new TecnicoRequest("Carlos", "carlos@teste.com", Perfil.TECNICO);
        TecnicoResponse response = new TecnicoResponse(1L, "Carlos", "carlos@teste.com", Perfil.TECNICO);
        given(service.criar(any(TecnicoRequest.class))).willReturn(response);

        mockMvc.perform(post("/tecnicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.perfil").value("TECNICO"));
    }

    @Test
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        TecnicoRequest request = new TecnicoRequest("Carlos", "email-invalido", Perfil.TECNICO);

        mockMvc.perform(post("/tecnicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoEmailDuplicado() throws Exception {
        TecnicoRequest request = new TecnicoRequest("Carlos", "carlos@teste.com", Perfil.TECNICO);
        given(service.criar(any(TecnicoRequest.class)))
                .willThrow(new RegraNegocioException("Já existe técnico com o e-mail carlos@teste.com"));

        mockMvc.perform(post("/tecnicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar404QuandoTecnicoNaoExiste() throws Exception {
        given(service.buscar(eq(99L))).willThrow(new RecursoNaoEncontradoException("Técnico 99 não encontrado"));

        mockMvc.perform(get("/tecnicos/99"))
                .andExpect(status().isNotFound());
    }
}
