package com.universidad.gestionacademica.controller;

import com.universidad.gestionacademica.exception.DatoDuplicadoException;
import com.universidad.gestionacademica.exception.EstudianteNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convierte las excepciones de la API en mensajes de error en JSON.
 */
@RestControllerAdvice(assignableTypes = EstudianteRestController.class)
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return respuesta(HttpStatus.BAD_REQUEST, "Datos inválidos", errores);
    }

    @ExceptionHandler(EstudianteNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(EstudianteNoEncontradoException ex) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> duplicado(DatoDuplicadoException ex) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), Map.of(ex.getCampo(), ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido", null);
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus estado, String mensaje,
                                                          Map<String, String> errores) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("estado", estado.value());
        cuerpo.put("mensaje", mensaje);
        if (errores != null) {
            cuerpo.put("errores", errores);
        }
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
