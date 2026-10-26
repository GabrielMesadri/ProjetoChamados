package br.com.sistemas.chamados.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.exception.ChamadoFechadoException;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;

@Service
public class ChamadoService {

    private final ChamadoRepository repository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final TecnicoRepository tecnicoRepository;

    public ChamadoService(ChamadoRepository repository, ClienteRepository clienteRepository,
            CategoriaRepository categoriaRepository, TecnicoRepository tecnicoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    @Transactional
    public ChamadoResponse criar(ChamadoRequest dto) {
        Cliente cliente = buscarCliente(dto.clienteId());
        Categoria categoria = buscarCategoria(dto.categoriaId());

        Chamado chamado = new Chamado(dto.titulo(), dto.descricao(), dto.prioridade(), cliente, categoria);
        if (dto.tecnicoId() != null) {
            chamado.setTecnico(buscarTecnico(dto.tecnicoId()));
        }

        return ChamadoResponse.de(repository.save(chamado));
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listar(StatusChamado status, Prioridade prioridade, Long clienteId, Pageable pageable) {
        return repository.buscarComFiltros(status, prioridade, clienteId, pageable).map(ChamadoResponse::de);
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponse> listarPorCliente(Long clienteId) {
        buscarCliente(clienteId);
        return repository.findByClienteId(clienteId).stream().map(ChamadoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ChamadoResponse buscar(Long id) {
        return ChamadoResponse.de(buscarEntity(id));
    }

    @Transactional
    public ChamadoResponse atualizar(Long id, ChamadoRequest dto) {
        Chamado chamado = buscarEntity(id);
        verificarAberto(chamado);

        chamado.setTitulo(dto.titulo());
        chamado.setDescricao(dto.descricao());
        chamado.setPrioridade(dto.prioridade());
        chamado.setCategoria(buscarCategoria(dto.categoriaId()));
        chamado.setTecnico(dto.tecnicoId() == null ? null : buscarTecnico(dto.tecnicoId()));

        aplicarStatus(chamado, dto.status() == null ? chamado.getStatus() : dto.status());

        return ChamadoResponse.de(chamado);
    }

    @Transactional
    public ChamadoResponse atribuirTecnico(Long id, Long tecnicoId) {
        Chamado chamado = buscarEntity(id);
        verificarAberto(chamado);
        chamado.setTecnico(buscarTecnico(tecnicoId));
        return ChamadoResponse.de(chamado);
    }

    @Transactional
    public ChamadoResponse alterarStatus(Long id, StatusChamado status) {
        Chamado chamado = buscarEntity(id);
        verificarAberto(chamado);
        aplicarStatus(chamado, status);
        return ChamadoResponse.de(chamado);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntity(id));
    }

    private void aplicarStatus(Chamado chamado, StatusChamado status) {
        chamado.setStatus(status);
        chamado.setDataFechamento(status == StatusChamado.FECHADO ? LocalDateTime.now() : null);
    }

    private void verificarAberto(Chamado chamado) {
        if (chamado.getStatus() == StatusChamado.FECHADO) {
            throw new ChamadoFechadoException("Chamado " + chamado.getId() + " está fechado e não pode ser alterado");
        }
    }

    private Chamado buscarEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Chamado " + id + " não encontrado"));
    }

    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente " + id + " não encontrado"));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria " + id + " não encontrada"));
    }

    private Tecnico buscarTecnico(Long id) {
        return tecnicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Técnico " + id + " não encontrado"));
    }
}
