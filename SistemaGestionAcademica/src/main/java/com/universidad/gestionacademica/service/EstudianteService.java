package com.universidad.gestionacademica.service;

import com.universidad.gestionacademica.exception.DatoDuplicadoException;
import com.universidad.gestionacademica.exception.EstudianteNoEncontradoException;
import com.universidad.gestionacademica.model.Estudiante;
import com.universidad.gestionacademica.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio de la gestión de estudiantes:
 * registrar, listar, buscar, actualizar y eliminar.
 */
@Service
@Transactional
public class EstudianteService {

    private final EstudianteRepository repositorio;

    public EstudianteService(EstudianteRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** 1. Registrar estudiante. */
    public Estudiante registrar(Estudiante estudiante) {
        normalizar(estudiante);
        if (repositorio.existsByCodigo(estudiante.getCodigo())) {
            throw new DatoDuplicadoException("codigo",
                    "Ya existe un estudiante con el código " + estudiante.getCodigo());
        }
        if (repositorio.existsByCorreoIgnoreCase(estudiante.getCorreo())) {
            throw new DatoDuplicadoException("correo",
                    "Ya existe un estudiante con el correo " + estudiante.getCorreo());
        }
        estudiante.setId(null);
        return repositorio.save(estudiante);
    }

    /** 2. Listar estudiantes ordenados por apellidos. */
    @Transactional(readOnly = true)
    public List<Estudiante> listar() {
        return repositorio.findAllByOrderByApellidosAscNombresAsc();
    }

    /** 3. Buscar estudiantes por código, nombres, apellidos o carrera. */
    @Transactional(readOnly = true)
    public List<Estudiante> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        return repositorio.buscar(texto.trim());
    }

    @Transactional(readOnly = true)
    public Estudiante obtenerPorId(Integer id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new EstudianteNoEncontradoException(id));
    }

    /** 4. Actualizar estudiante. */
    public Estudiante actualizar(Integer id, Estudiante datos) {
        Estudiante existente = obtenerPorId(id);
        normalizar(datos);
        if (repositorio.existsByCodigoAndIdNot(datos.getCodigo(), id)) {
            throw new DatoDuplicadoException("codigo",
                    "Ya existe otro estudiante con el código " + datos.getCodigo());
        }
        if (repositorio.existsByCorreoIgnoreCaseAndIdNot(datos.getCorreo(), id)) {
            throw new DatoDuplicadoException("correo",
                    "Ya existe otro estudiante con el correo " + datos.getCorreo());
        }
        existente.setCodigo(datos.getCodigo());
        existente.setNombres(datos.getNombres());
        existente.setApellidos(datos.getApellidos());
        existente.setCorreo(datos.getCorreo());
        existente.setCarrera(datos.getCarrera());
        existente.setSemestre(datos.getSemestre());
        return repositorio.save(existente);
    }

    /** 5. Eliminar estudiante. */
    public void eliminar(Integer id) {
        Estudiante existente = obtenerPorId(id);
        repositorio.delete(existente);
    }

    /** Limpia espacios y unifica formato antes de guardar. */
    private void normalizar(Estudiante e) {
        if (e.getCodigo() != null) {
            e.setCodigo(e.getCodigo().trim().toUpperCase());
        }
        if (e.getNombres() != null) {
            e.setNombres(e.getNombres().trim().replaceAll("\\s+", " "));
        }
        if (e.getApellidos() != null) {
            e.setApellidos(e.getApellidos().trim().replaceAll("\\s+", " "));
        }
        if (e.getCorreo() != null) {
            e.setCorreo(e.getCorreo().trim().toLowerCase());
        }
        if (e.getCarrera() != null) {
            e.setCarrera(e.getCarrera().trim());
        }
    }
}
