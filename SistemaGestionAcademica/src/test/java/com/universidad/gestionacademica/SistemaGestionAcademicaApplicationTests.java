package com.universidad.gestionacademica;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración: levanta la aplicación completa (web + JPA + H2)
 * y recorre el ciclo CRUD a través de la API REST.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SistemaGestionAcademicaApplicationTests {

    private static final String JSON = """
            {"codigo":"U20259999","nombres":"Pedro","apellidos":"Salas Cruz",
             "correo":"pedro.salas@universidad.edu.pe","carrera":"Contabilidad","semestre":%d}
            """;

    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    @DisplayName("La aplicación inicia y responde al health check")
    void contextoYSalud() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @Order(2)
    @DisplayName("Ciclo completo: registrar, consultar, actualizar y eliminar")
    void cicloCrudCompleto() throws Exception {
        // Registrar
        String respuesta = mvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.formatted(1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Estudiante registrado correctamente"))
                .andReturn().getResponse().getContentAsString();
        int id = Integer.parseInt(respuesta.replaceAll(".*\"id\":(\\d+).*", "$1"));

        // Duplicado -> 409
        mvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.formatted(1)))
                .andExpect(status().isConflict());

        // Listar y buscar
        mvc.perform(get("/api/estudiantes").param("q", "salas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Actualizar
        mvc.perform(put("/api/estudiantes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.formatted(2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estudiante.semestre").value(2));

        // Eliminar
        mvc.perform(delete("/api/estudiantes/" + id))
                .andExpect(status().isOk());
        mvc.perform(get("/api/estudiantes/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @DisplayName("La API rechaza datos inválidos con mensajes por campo")
    void apiValidaDatos() throws Exception {
        mvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"x\",\"correo\":\"malo\",\"semestre\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.codigo").exists())
                .andExpect(jsonPath("$.errores.correo").exists())
                .andExpect(jsonPath("$.errores.semestre").exists());
    }
}
