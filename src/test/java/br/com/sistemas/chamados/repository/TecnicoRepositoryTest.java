package br.com.sistemas.chamados.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import br.com.sistemas.chamados.entity.Perfil;
import br.com.sistemas.chamados.entity.Tecnico;

@DataJpaTest
class TecnicoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TecnicoRepository repository;

    @Test
    void deveIndicarQuandoEmailJaExiste() {
        entityManager.persistAndFlush(new Tecnico("Carlos", "carlos@teste.com", Perfil.TECNICO));

        assertThat(repository.existsByEmail("carlos@teste.com")).isTrue();
        assertThat(repository.existsByEmail("outro@teste.com")).isFalse();
    }

    @Test
    void deveIgnorarOProprioIdAoVerificarEmailDuplicado() {
        Tecnico tecnico = entityManager.persistAndFlush(new Tecnico("Ana", "ana@teste.com", Perfil.ADMIN));

        assertThat(repository.existsByEmailAndIdNot("ana@teste.com", tecnico.getId())).isFalse();
        assertThat(repository.existsByEmailAndIdNot("ana@teste.com", tecnico.getId() + 1)).isTrue();
    }
}
