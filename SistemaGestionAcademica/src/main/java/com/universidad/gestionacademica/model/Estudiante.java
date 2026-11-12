package com.universidad.gestionacademica.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entidad que representa a un estudiante universitario.
 * Las anotaciones de validación implementan la funcionalidad
 * "Validar los datos ingresados".
 */
@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^[A-Z]\\d{8}$", message = "El código debe tener una letra mayúscula seguida de 8 dígitos (ej. U20231234)")
    @Column(nullable = false, unique = true, length = 9)
    private String codigo;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 60, message = "Los nombres deben tener entre 2 y 60 caracteres")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Los nombres solo pueden contener letras y espacios")
    @Column(nullable = false, length = 60)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 60, message = "Los apellidos deben tener entre 2 y 60 caracteres")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Los apellidos solo pueden contener letras y espacios")
    @Column(nullable = false, length = 60)
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @NotBlank(message = "La carrera es obligatoria")
    @Size(max = 80, message = "La carrera no puede superar los 80 caracteres")
    @Column(nullable = false, length = 80)
    private String carrera;

    @NotNull(message = "El semestre es obligatorio")
    @Min(value = 1, message = "El semestre mínimo es 1")
    @Max(value = 12, message = "El semestre máximo es 12")
    @Column(nullable = false)
    private Integer semestre;

    public Estudiante() {
    }

    public Estudiante(String codigo, String nombres, String apellidos,
                      String correo, String carrera, Integer semestre) {
        this.codigo = codigo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.carrera = carrera;
        this.semestre = semestre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    /** Nombre completo para mostrar en las vistas. */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
