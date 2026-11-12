package com.universidad.gestionacademica.config;

import com.universidad.gestionacademica.model.Estudiante;
import com.universidad.gestionacademica.repository.EstudianteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Carga estudiantes de ejemplo al iniciar (solo si la tabla está vacía).
 * Se desactiva con app.datos-demo=false.
 */
@Component
@ConditionalOnProperty(name = "app.datos-demo", havingValue = "true", matchIfMissing = true)
public class DatosIniciales implements CommandLineRunner {

    private final EstudianteRepository repositorio;

    public DatosIniciales(EstudianteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) {
            return;
        }
        repositorio.saveAll(List.of(
                new Estudiante("U20231001", "María Fernanda", "Quispe Huamán",
                        "maria.quispe@universidad.edu.pe", "Ingeniería de Sistemas", 5),
                new Estudiante("U20222045", "José Luis", "Ramírez Torres",
                        "jose.ramirez@universidad.edu.pe", "Ingeniería de Software", 7),
                new Estudiante("U20240310", "Ana Lucía", "Mendoza Rojas",
                        "ana.mendoza@universidad.edu.pe", "Administración", 3)));
    }
}
