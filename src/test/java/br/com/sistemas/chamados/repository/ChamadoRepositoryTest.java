package br.com.sistemas.chamados.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;

@DataJpaTest
class ChamadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChamadoRepository repository;

    @Test
    void deveFiltrarPorStatusPrioridadeEClienteId() {
        Cliente cliente = entityManager.persistAndFlush(new Cliente("Maria", "maria@teste.com", null));
        Categoria categoria = entityManager.persistAndFlush(new Categoria("Rede", "Problemas de rede"));

        Chamado abertoAlta = new Chamado("Sem internet", "Conexão caiu", Prioridade.ALTA, cliente, categoria);
        Chamado fechadoBaixa = new Chamado("Impressora", "Sem tinta", Prioridade.BAIXA, cliente, categoria);
        fechadoBaixa.setStatus(StatusChamado.FECHADO);
        entityManager.persistAndFlush(abertoAlta);
        entityManager.persistAndFlush(fechadoBaixa);

        Page<Chamado> porStatus = repository.buscarComFiltros(StatusChamado.ABERTO, null, null, PageRequest.of(0, 10));
        Page<Chamado> porPrioridade = repository.buscarComFiltros(null, Prioridade.BAIXA, null, PageRequest.of(0, 10));
        Page<Chamado> porCliente = repository.buscarComFiltros(null, null, cliente.getId(), PageRequest.of(0, 10));

        assertThat(porStatus.getContent()).hasSize(1).extracting(Chamado::getTitulo).containsExactly("Sem internet");
        assertThat(porPrioridade.getContent()).hasSize(1).extracting(Chamado::getTitulo).containsExactly("Impressora");
        assertThat(porCliente.getContent()).hasSize(2);
    }

    @Test
    void deveListarChamadosPorCliente() {
        Cliente cliente = entityManager.persistAndFlush(new Cliente("João", "joao@teste.com", null));
        Cliente outroCliente = entityManager.persistAndFlush(new Cliente("Ana", "ana@teste.com", null));
        Categoria categoria = entityManager.persistAndFlush(new Categoria("Software", "Problemas de software"));

        entityManager.persistAndFlush(new Chamado("Erro no sistema", "Tela travando", Prioridade.MEDIA, cliente, categoria));
        entityManager.persistAndFlush(new Chamado("Login não funciona", "Senha incorreta", Prioridade.CRITICA, outroCliente, categoria));

        assertThat(repository.findByClienteId(cliente.getId())).hasSize(1);
        assertThat(repository.findByClienteId(outroCliente.getId())).hasSize(1);
    }
}
