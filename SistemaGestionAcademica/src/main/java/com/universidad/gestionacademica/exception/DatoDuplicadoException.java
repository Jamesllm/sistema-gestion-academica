package com.universidad.gestionacademica.exception;

/**
 * Se lanza cuando el código o el correo ya pertenecen a otro estudiante.
 */
public class DatoDuplicadoException extends RuntimeException {

    private final String campo;

    public DatoDuplicadoException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
