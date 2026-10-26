package br.com.sistemas.chamados.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Comentario;
import br.com.sistemas.chamados.entity.Prioridade;

@DataJpaTest
class ComentarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ComentarioRepository repository;

    @Test
    void deveListarComentariosDoChamadoEmOrdemCronologica() {
        Cliente cliente = entityManager.persistAndFlush(new Cliente("Maria", "maria@teste.com", null));
        Categoria categoria = entityManager.persistAndFlush(new Categoria("Rede", "Problemas de rede"));
        Chamado chamado = entityManager.persistAndFlush(
                new Chamado("Sem internet", "Conexão caiu", Prioridade.ALTA, cliente, categoria));
        Chamado outroChamado = entityManager.persistAndFlush(
                new Chamado("Impressora", "Sem tinta", Prioridade.BAIXA, cliente, categoria));

        entityManager.persistAndFlush(new Comentario("Primeiro contato", "Carlos", chamado));
        entityManager.persistAndFlush(new Comentario("Segundo contato", "Carlos", chamado));
        entityManager.persistAndFlush(new Comentario("Comentário de outro chamado", "Ana", outroChamado));

        assertThat(repository.findByChamadoIdOrderByDataAsc(chamado.getId()))
                .hasSize(2)
                .extracting(Comentario::getTexto)
                .containsExactly("Primeiro contato", "Segundo contato");
    }
}
