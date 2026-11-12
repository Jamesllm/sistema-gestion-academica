package com.universidad.gestionacademica.service;

import com.universidad.gestionacademica.exception.DatoDuplicadoException;
import com.universidad.gestionacademica.exception.EstudianteNoEncontradoException;
import com.universidad.gestionacademica.model.Estudiante;
import com.universidad.gestionacademica.repository.EstudianteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de la lógica de negocio (el repositorio se simula con Mockito).
 */
@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository repositorio;

    @InjectMocks
    private EstudianteService servicio;

    private Estudiante nuevoEstudiante() {
        return new Estudiante(" u20231234 ", "  Carlos   Alberto ", "Pérez Díaz",
                "CARLOS.PEREZ@Universidad.edu.pe ", "Ingeniería de Sistemas", 4);
    }

    @Test
    @DisplayName("Registrar: guarda el estudiante y normaliza sus datos")
    void registrarEstudianteValido() {
        when(repositorio.existsByCodigo("U20231234")).thenReturn(false);
        when(repositorio.existsByCorreoIgnoreCase("carlos.perez@universidad.edu.pe")).thenReturn(false);
        when(repositorio.save(any(Estudiante.class))).thenAnswer(inv -> {
            Estudiante e = inv.getArgument(0);
            e.setId(1);
            return e;
        });

        Estudiante guardado = servicio.registrar(nuevoEstudiante());

        assertThat(guardado.getId()).isEqualTo(1);
        assertThat(guardado.getCodigo()).isEqualTo("U20231234");
        assertThat(guardado.getNombres()).isEqualTo("Carlos Alberto");
        assertThat(guardado.getCorreo()).isEqualTo("carlos.perez@universidad.edu.pe");
    }

    @Test
    @DisplayName("Registrar: rechaza un código duplicado")
    void registrarCodigoDuplicado() {
        when(repositorio.existsByCodigo("U20231234")).thenReturn(true);

        assertThatThrownBy(() -> servicio.registrar(nuevoEstudiante()))
                .isInstanceOf(DatoDuplicadoException.class)
                .hasMessageContaining("U20231234");
        verify(repositorio, never()).save(any());
    }

    @Test
    @DisplayName("Registrar: rechaza un correo duplicado")
    void registrarCorreoDuplicado() {
        when(repositorio.existsByCodigo("U20231234")).thenReturn(false);
        when(repositorio.existsByCorreoIgnoreCase("carlos.perez@universidad.edu.pe")).thenReturn(true);

        assertThatThrownBy(() -> servicio.registrar(nuevoEstudiante()))
                .isInstanceOf(DatoDuplicadoException.class)
                .extracting("campo").isEqualTo("correo");
        verify(repositorio, never()).save(any());
    }

    @Test
    @DisplayName("Buscar: sin texto devuelve el listado completo")
    void buscarSinTextoListaTodos() {
        List<Estudiante> todos = List.of(nuevoEstudiante());
        when(repositorio.findAllByOrderByApellidosAscNombresAsc()).thenReturn(todos);

        assertThat(servicio.buscar("   ")).isEqualTo(todos);
        verify(repositorio, never()).buscar(any());
    }

    @Test
    @DisplayName("Buscar: con texto delega la consulta al repositorio")
    void buscarConTexto() {
        when(repositorio.buscar("Pérez")).thenReturn(List.of(nuevoEstudiante()));

        assertThat(servicio.buscar(" Pérez ")).hasSize(1);
    }

    @Test
    @DisplayName("Actualizar: modifica los datos de un estudiante existente")
    void actualizarEstudiante() {
        Estudiante existente = nuevoEstudiante();
        existente.setId(5);
        when(repositorio.findById(5)).thenReturn(Optional.of(existente));
        when(repositorio.existsByCodigoAndIdNot("U20231234", 5)).thenReturn(false);
        when(repositorio.existsByCorreoIgnoreCaseAndIdNot("carlos.perez@universidad.edu.pe", 5)).thenReturn(false);
        when(repositorio.save(any(Estudiante.class))).thenAnswer(inv -> inv.getArgument(0));

        Estudiante cambios = nuevoEstudiante();
        cambios.setSemestre(6);
        cambios.setCarrera("Ingeniería de Software");

        Estudiante actualizado = servicio.actualizar(5, cambios);

        assertThat(actualizado.getId()).isEqualTo(5);
        assertThat(actualizado.getSemestre()).isEqualTo(6);
        assertThat(actualizado.getCarrera()).isEqualTo("Ingeniería de Software");
    }

    @Test
    @DisplayName("Actualizar: falla si el estudiante no existe")
    void actualizarInexistente() {
        when(repositorio.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.actualizar(99, nuevoEstudiante()))
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Eliminar: borra al estudiante existente")
    void eliminarEstudiante() {
        Estudiante existente = nuevoEstudiante();
        existente.setId(3);
        when(repositorio.findById(3)).thenReturn(Optional.of(existente));

        servicio.eliminar(3);

        verify(repositorio).delete(existente);
    }

    @Test
    @DisplayName("Eliminar: falla si el estudiante no existe")
    void eliminarInexistente() {
        when(repositorio.findById(7)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.eliminar(7))
                .isInstanceOf(EstudianteNoEncontradoException.class);
        verify(repositorio, never()).delete(any());
    }
}
