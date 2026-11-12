package com.universidad.gestionacademica.controller;

import com.universidad.gestionacademica.model.Estudiante;
import com.universidad.gestionacademica.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * API REST de estudiantes (JSON). Útil para pruebas automatizadas
 * y para verificar el despliegue desde la línea de comandos.
 */
@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteRestController {

    private final EstudianteService servicio;

    public EstudianteRestController(EstudianteService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Estudiante> listar(@RequestParam(name = "q", required = false) String q) {
        return servicio.buscar(q);
    }

    @GetMapping("/{id}")
    public Estudiante obtener(@PathVariable Integer id) {
        return servicio.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody Estudiante estudiante) {
        Estudiante guardado = servicio.registrar(estudiante);
        return ResponseEntity.created(URI.create("/api/estudiantes/" + guardado.getId()))
                .body(Map.of("mensaje", "Estudiante registrado correctamente", "estudiante", guardado));
    }

    @PutMapping("/{id}")
    public Map<String, Object> actualizar(@PathVariable Integer id,
                                          @Valid @RequestBody Estudiante estudiante) {
        Estudiante actualizado = servicio.actualizar(id, estudiante);
        return Map.of("mensaje", "Estudiante actualizado correctamente", "estudiante", actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        servicio.eliminar(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("mensaje", "Estudiante eliminado correctamente"));
    }
}
