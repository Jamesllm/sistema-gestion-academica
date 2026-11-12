package com.universidad.gestionacademica.exception;

/**
 * Se lanza cuando se solicita un estudiante que no existe.
 */
public class EstudianteNoEncontradoException extends RuntimeException {

    public EstudianteNoEncontradoException(Integer id) {
        super("No existe un estudiante con id " + id);
    }
}
