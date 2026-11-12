package com.universidad.gestionacademica.repository;

import com.universidad.gestionacademica.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de estudiantes (Spring Data JPA genera la implementación).
 */
public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {

    Optional<Estudiante> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCodigoAndIdNot(String codigo, Integer id);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Integer id);

    /** Búsqueda por código, nombres, apellidos o carrera (sin distinguir mayúsculas). */
    @Query("""
            SELECT e FROM Estudiante e
            WHERE LOWER(e.codigo)    LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(e.nombres)   LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(e.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(e.carrera)   LIKE LOWER(CONCAT('%', :texto, '%'))
            ORDER BY e.apellidos, e.nombres
            """)
    List<Estudiante> buscar(@Param("texto") String texto);

    List<Estudiante> findAllByOrderByApellidosAscNombresAsc();
}
