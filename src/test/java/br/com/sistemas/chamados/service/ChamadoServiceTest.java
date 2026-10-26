package br.com.sistemas.chamados.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository repository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private TecnicoRepository tecnicoRepository;

    private ChamadoService service;

    private Chamado chamadoAberto;
    private Chamado chamadoFechado;

    @BeforeEach
    void setUp() {
        service = new ChamadoService(repository, clienteRepository, categoriaRepository, tecnicoRepository);

        Cliente cliente = new Cliente("Maria", "maria@teste.com", null);
        Categoria categoria = new Categoria("Rede", "Problemas de rede");

        chamadoAberto = new Chamado("Sem internet", "Conexão caiu", Prioridade.ALTA, cliente, categoria);

        chamadoFechado = new Chamado("Impressora", "Sem tinta", Prioridade.BAIXA, cliente, categoria);
        chamadoFechado.setStatus(StatusChamado.FECHADO);
    }

    @Test
    void deveTransicionarDeAbertoParaEmAtendimento() {
        given(repository.findById(1L)).willReturn(Optional.of(chamadoAberto));

        service.alterarStatus(1L, StatusChamado.EM_ATENDIMENTO);

        assertThat(chamadoAberto.getStatus()).isEqualTo(StatusChamado.EM_ATENDIMENTO);
        assertThat(chamadoAberto.getDataFechamento()).isNull();
    }

    @Test
    void devePreencherDataFechamentoAoFechar() {
        given(repository.findById(1L)).willReturn(Optional.of(chamadoAberto));

        service.alterarStatus(1L, StatusChamado.FECHADO);

        assertThat(chamadoAberto.getStatus()).isEqualTo(StatusChamado.FECHADO);
        assertThat(chamadoAberto.getDataFechamento()).isNotNull();
    }

    @Test
    void naoDevePermitirAlterarStatusDeChamadoJaFechado() {
        given(repository.findById(2L)).willReturn(Optional.of(chamadoFechado));

        assertThatThrownBy(() -> service.alterarStatus(2L, StatusChamado.ABERTO))
                .isInstanceOf(ChamadoFechadoException.class);
    }

    @Test
    void deveAtribuirTecnicoAChamadoAberto() {
        Tecnico tecnico = new Tecnico("Carlos", "carlos@teste.com", br.com.sistemas.chamados.entity.Perfil.TECNICO);
        given(repository.findById(1L)).willReturn(Optional.of(chamadoAberto));
        given(tecnicoRepository.findById(5L)).willReturn(Optional.of(tecnico));

        service.atribuirTecnico(1L, 5L);

        assertThat(chamadoAberto.getTecnico()).isEqualTo(tecnico);
    }

    @Test
    void naoDevePermitirAtribuirTecnicoAChamadoFechado() {
        given(repository.findById(2L)).willReturn(Optional.of(chamadoFechado));

        assertThatThrownBy(() -> service.atribuirTecnico(2L, 5L))
                .isInstanceOf(ChamadoFechadoException.class);

        verify(tecnicoRepository, never()).findById(anyLong());
    }

    @Test
    void naoDevePermitirAtualizarChamadoFechado() {
        given(repository.findById(2L)).willReturn(Optional.of(chamadoFechado));

        assertThatThrownBy(() -> service.atualizar(2L, null))
                .isInstanceOf(ChamadoFechadoException.class);

        verify(categoriaRepository, never()).findById(anyLong());
    }
}
