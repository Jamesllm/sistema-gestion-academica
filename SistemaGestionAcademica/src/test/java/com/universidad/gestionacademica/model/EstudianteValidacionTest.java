package com.universidad.gestionacademica.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de las reglas de validación de los datos del estudiante.
 */
class EstudianteValidacionTest {

    private static ValidatorFactory fabrica;
    private static Validator validador;

    @BeforeAll
    static void iniciar() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validador = fabrica.getValidator();
    }

    @AfterAll
    static void cerrar() {
        fabrica.close();
    }

    private Estudiante valido() {
        return new Estudiante("U20231234", "Lucía", "Gómez Vargas",
                "lucia.gomez@universidad.edu.pe", "Derecho", 2);
    }

    private Set<String> camposConError(Estudiante e) {
        return validador.validate(e).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Test
    @DisplayName("Un estudiante con datos correctos no tiene errores")
    void estudianteValido() {
        assertThat(validador.validate(valido())).isEmpty();
    }

    @Test
    @DisplayName("Todos los campos vacíos son rechazados")
    void camposObligatorios() {
        Estudiante vacio = new Estudiante();
        assertThat(camposConError(vacio))
                .contains("codigo", "nombres", "apellidos", "correo", "carrera", "semestre");
    }

    @ParameterizedTest
    @ValueSource(strings = {"20231234", "u20231234", "U2023", "U202312345", "UX0231234"})
    @DisplayName("Códigos con formato incorrecto son rechazados")
    void codigoInvalido(String codigo) {
        Estudiante e = valido();
        e.setCodigo(codigo);
        assertThat(camposConError(e)).containsExactly("codigo");
    }

    @ParameterizedTest
    @ValueSource(strings = {"correo-sin-arroba", "a@", "@dominio.com"})
    @DisplayName("Correos con formato incorrecto son rechazados")
    void correoInvalido(String correo) {
        Estudiante e = valido();
        e.setCorreo(correo);
        assertThat(camposConError(e)).containsExactly("correo");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 13})
    @DisplayName("El semestre debe estar entre 1 y 12")
    void semestreFueraDeRango(int semestre) {
        Estudiante e = valido();
        e.setSemestre(semestre);
        assertThat(camposConError(e)).containsExactly("semestre");
    }

    @Test
    @DisplayName("Los nombres no admiten números")
    void nombresConNumeros() {
        Estudiante e = valido();
        e.setNombres("Lucía 2");
        assertThat(camposConError(e)).containsExactly("nombres");
    }
}
