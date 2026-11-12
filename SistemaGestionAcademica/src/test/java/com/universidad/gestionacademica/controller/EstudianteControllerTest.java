package com.universidad.gestionacademica.controller;

import com.universidad.gestionacademica.exception.DatoDuplicadoException;
import com.universidad.gestionacademica.exception.EstudianteNoEncontradoException;
import com.universidad.gestionacademica.model.Estudiante;
import com.universidad.gestionacademica.service.EstudianteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas de la capa web: vistas, validación y mensajes de éxito/error.
 */
@WebMvcTest(EstudianteController.class)
@ActiveProfiles("test")
class EstudianteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EstudianteService servicio;

    private Estudiante ejemplo() {
        Estudiante e = new Estudiante("U20231001", "María", "Quispe",
                "maria.quispe@universidad.edu.pe", "Ingeniería de Sistemas", 5);
        e.setId(1);
        return e;
    }

    @Test
    @DisplayName("GET /estudiantes muestra el listado")
    void listar() throws Exception {
        when(servicio.buscar(null)).thenReturn(List.of(ejemplo()));

        mvc.perform(get("/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudiantes/lista"))
                .andExpect(model().attribute("estudiantes", hasSize(1)))
                .andExpect(content().string(containsString("U20231001")));
    }

    @Test
    @DisplayName("GET /estudiantes?q= filtra por el texto ingresado")
    void buscar() throws Exception {
        when(servicio.buscar("quispe")).thenReturn(List.of(ejemplo()));

        mvc.perform(get("/estudiantes").param("q", "quispe"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Quispe")));
    }

    @Test
    @DisplayName("POST válido registra y redirige con mensaje de éxito")
    void registrarValido() throws Exception {
        when(servicio.registrar(any(Estudiante.class))).thenReturn(ejemplo());

        mvc.perform(post("/estudiantes")
                        .param("codigo", "U20231001")
                        .param("nombres", "María")
                        .param("apellidos", "Quispe")
                        .param("correo", "maria.quispe@universidad.edu.pe")
                        .param("carrera", "Ingeniería de Sistemas")
                        .param("semestre", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudiantes"))
                .andExpect(flash().attribute("exito", containsString("registrado correctamente")));
    }

    @Test
    @DisplayName("POST inválido vuelve al formulario mostrando los errores")
    void registrarInvalido() throws Exception {
        mvc.perform(post("/estudiantes")
                        .param("codigo", "123")
                        .param("nombres", "")
                        .param("apellidos", "Quispe")
                        .param("correo", "no-es-correo")
                        .param("carrera", "")
                        .param("semestre", "20"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudiantes/formulario"))
                .andExpect(model().attributeHasFieldErrors("estudiante",
                        "codigo", "nombres", "correo", "carrera", "semestre"))
                .andExpect(content().string(containsString("Revise los datos ingresados")));
        verify(servicio, never()).registrar(any());
    }

    @Test
    @DisplayName("POST con código duplicado muestra el error en el formulario")
    void registrarDuplicado() throws Exception {
        when(servicio.registrar(any(Estudiante.class)))
                .thenThrow(new DatoDuplicadoException("codigo", "Ya existe un estudiante con el código U20231001"));

        mvc.perform(post("/estudiantes")
                        .param("codigo", "U20231001")
                        .param("nombres", "María")
                        .param("apellidos", "Quispe")
                        .param("correo", "otra@universidad.edu.pe")
                        .param("carrera", "Derecho")
                        .param("semestre", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("estudiante", "codigo"))
                .andExpect(content().string(containsString("Ya existe un estudiante")));
    }

    @Test
    @DisplayName("POST /{id} actualiza y redirige con mensaje de éxito")
    void actualizar() throws Exception {
        when(servicio.actualizar(eq(1), any(Estudiante.class))).thenReturn(ejemplo());

        mvc.perform(post("/estudiantes/1")
                        .param("codigo", "U20231001")
                        .param("nombres", "María")
                        .param("apellidos", "Quispe")
                        .param("correo", "maria.quispe@universidad.edu.pe")
                        .param("carrera", "Ingeniería de Sistemas")
                        .param("semestre", "6"))
                .andExpect(redirectedUrl("/estudiantes"))
                .andExpect(flash().attribute("exito", containsString("actualizado correctamente")));
    }

    @Test
    @DisplayName("POST /{id}/eliminar elimina y muestra mensaje")
    void eliminar() throws Exception {
        mvc.perform(post("/estudiantes/1/eliminar"))
                .andExpect(redirectedUrl("/estudiantes"))
                .andExpect(flash().attribute("exito", "Estudiante eliminado correctamente."));
        verify(servicio).eliminar(1);
    }

    @Test
    @DisplayName("Eliminar un estudiante inexistente muestra mensaje de error")
    void eliminarInexistente() throws Exception {
        doThrow(new EstudianteNoEncontradoException(50)).when(servicio).eliminar(50);

        mvc.perform(post("/estudiantes/50/eliminar"))
                .andExpect(redirectedUrl("/estudiantes"))
                .andExpect(flash().attribute("error", "No existe un estudiante con id 50"));
    }
}
