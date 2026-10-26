package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import br.com.sistemas.chamados.dto.AlterarStatusRequest;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.service.ChamadoService;

@WebMvcTest(ChamadoController.class)
class ChamadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChamadoService service;

    @Test
    void deveCriarChamadoComSucesso() throws Exception {
        ChamadoRequest request = new ChamadoRequest(
                "Sem internet", "Conexão caiu", Prioridade.ALTA, null, 1L, 2L, null);
        ChamadoResponse response = new ChamadoResponse(
                1L, "Sem internet", "Conexão caiu", LocalDateTime.now(), null,
                Prioridade.ALTA, StatusChamado.ABERTO, 1L, "Maria", 2L, "Rede", null, null);
        given(service.criar(any(ChamadoRequest.class))).willReturn(response);

        mockMvc.perform(post("/chamados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ABERTO"));
    }

    @Test
    void deveRetornar400QuandoTituloEstiverEmBranco() throws Exception {
        ChamadoRequest request = new ChamadoRequest(
                "", "Conexão caiu", Prioridade.ALTA, null, 1L, 2L, null);

        mockMvc.perform(post("/chamados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoChamadoNaoExiste() throws Exception {
        given(service.buscar(eq(99L))).willThrow(new RecursoNaoEncontradoException("Chamado 99 não encontrado"));

        mockMvc.perform(get("/chamados/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAlterarStatusComSucesso() throws Exception {
        AlterarStatusRequest request = new AlterarStatusRequest(StatusChamado.EM_ATENDIMENTO);
        ChamadoResponse response = new ChamadoResponse(
                1L, "Sem internet", "Conexão caiu", LocalDateTime.now(), null,
                Prioridade.ALTA, StatusChamado.EM_ATENDIMENTO, 1L, "Maria", 2L, "Rede", 3L, "Carlos");
        given(service.alterarStatus(eq(1L), eq(StatusChamado.EM_ATENDIMENTO))).willReturn(response);

        mockMvc.perform(patch("/chamados/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ATENDIMENTO"));
    }

    @Test
    void deveRetornar409QuandoChamadoJaEstiverFechado() throws Exception {
        AlterarStatusRequest request = new AlterarStatusRequest(StatusChamado.ABERTO);
        given(service.alterarStatus(eq(1L), eq(StatusChamado.ABERTO)))
                .willThrow(new ChamadoFechadoException("Chamado 1 está fechado e não pode ser alterado"));

        mockMvc.perform(patch("/chamados/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
