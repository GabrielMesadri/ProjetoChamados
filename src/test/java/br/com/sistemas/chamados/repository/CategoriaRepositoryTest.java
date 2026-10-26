package br.com.sistemas.chamados.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.sistemas.chamados.entity.Categoria;

@DataJpaTest
class CategoriaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoriaRepository repository;

    @Test
    void deveIndicarQuandoNomeJaExiste() {
        entityManager.persistAndFlush(new Categoria("Hardware", "Problemas de hardware"));

        assertThat(repository.existsByNome("Hardware")).isTrue();
        assertThat(repository.existsByNome("Software")).isFalse();
    }

    @Test
    void deveIgnorarOProprioIdAoVerificarNomeDuplicado() {
        Categoria categoria = entityManager.persistAndFlush(new Categoria("Rede", "Problemas de rede"));

        assertThat(repository.existsByNomeAndIdNot("Rede", categoria.getId())).isFalse();
        assertThat(repository.existsByNomeAndIdNot("Rede", categoria.getId() + 1)).isTrue();
    }
}
