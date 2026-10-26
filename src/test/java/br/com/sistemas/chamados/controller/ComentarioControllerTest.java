package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.dto.ComentarioResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.service.ComentarioService;

@WebMvcTest(ComentarioController.class)
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ComentarioService service;

    @Test
    void deveCriarComentarioComSucesso() throws Exception {
        ComentarioRequest request = new ComentarioRequest("Primeiro contato", "Carlos");
        ComentarioResponse response = new ComentarioResponse(1L, "Primeiro contato", LocalDateTime.now(), "Carlos", 10L);
        given(service.criar(eq(10L), any(ComentarioRequest.class))).willReturn(response);

        mockMvc.perform(post("/chamados/10/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.texto").value("Primeiro contato"));
    }

    @Test
    void deveRetornar400QuandoTextoEstiverEmBranco() throws Exception {
        ComentarioRequest request = new ComentarioRequest("", "Carlos");

        mockMvc.perform(post("/chamados/10/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoChamadoNaoExiste() throws Exception {
        ComentarioRequest request = new ComentarioRequest("Primeiro contato", "Carlos");
        given(service.criar(eq(99L), any(ComentarioRequest.class)))
                .willThrow(new RecursoNaoEncontradoException("Chamado 99 não encontrado"));

        mockMvc.perform(post("/chamados/99/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarComentariosDoChamado() throws Exception {
        ComentarioResponse resposta = new ComentarioResponse(1L, "Primeiro contato", LocalDateTime.now(), "Carlos", 10L);
        given(service.listar(10L)).willReturn(List.of(resposta));

        mockMvc.perform(get("/chamados/10/comentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].autor").value("Carlos"));
    }
}
