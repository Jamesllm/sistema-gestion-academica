package com.universidad.gestionacademica.repository;

import com.universidad.gestionacademica.model.Estudiante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas del repositorio contra una base de datos H2 en memoria.
 */
@DataJpaTest
@ActiveProfiles("test")
class EstudianteRepositoryTest {

    @Autowired
    private EstudianteRepository repositorio;

    private Estudiante ana;

    @BeforeEach
    void preparar() {
        ana = repositorio.save(new Estudiante("U20240001", "Ana", "Torres Lima",
                "ana.torres@universidad.edu.pe", "Psicología", 1));
        repositorio.save(new Estudiante("U20240002", "Bruno", "Alva Soto",
                "bruno.alva@universidad.edu.pe", "Ingeniería Civil", 8));
    }

    @Test
    @DisplayName("Lista los estudiantes ordenados por apellidos")
    void listarOrdenado() {
        List<Estudiante> lista = repositorio.findAllByOrderByApellidosAscNombresAsc();
        assertThat(lista).extracting(Estudiante::getApellidos)
                .containsExactly("Alva Soto", "Torres Lima");
    }

    @Test
    @DisplayName("Busca sin distinguir mayúsculas por nombre, código o carrera")
    void buscarPorTexto() {
        assertThat(repositorio.buscar("ANA")).extracting(Estudiante::getCodigo).containsExactly("U20240001");
        assertThat(repositorio.buscar("20240002")).hasSize(1);
        assertThat(repositorio.buscar("civil")).extracting(Estudiante::getNombres).containsExactly("Bruno");
        assertThat(repositorio.buscar("medicina")).isEmpty();
    }

    @Test
    @DisplayName("Detecta códigos y correos ya registrados")
    void detectarDuplicados() {
        assertThat(repositorio.existsByCodigo("U20240001")).isTrue();
        assertThat(repositorio.existsByCorreoIgnoreCase("ANA.TORRES@universidad.edu.pe")).isTrue();
        assertThat(repositorio.existsByCodigoAndIdNot("U20240001", ana.getId())).isFalse();
        assertThat(repositorio.existsByCorreoIgnoreCaseAndIdNot("bruno.alva@universidad.edu.pe", ana.getId())).isTrue();
    }
}
