package com.empleados.sistema.repository;

import com.empleados.sistema.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    
    // Buscar proyecto por nombre
    Optional<Proyecto> findByNombre(String nombre);
    
    // Verificar si existe un proyecto con ese nombre
    boolean existsByNombre(String nombre);
    
    // Buscar proyectos activos (fecha fin mayor a hoy o nula)
    @Query("SELECT p FROM Proyecto p WHERE p.fechaFin IS NULL OR p.fechaFin > CURRENT_DATE")
    List<Proyecto> findProyectosActivos();
    
    // Buscar proyectos inactivos (fecha fin menor a hoy)
    @Query("SELECT p FROM Proyecto p WHERE p.fechaFin IS NOT NULL AND p.fechaFin < CURRENT_DATE")
    List<Proyecto> findProyectosInactivos();
    
    // Buscar proyectos por rango de fechas de inicio
    List<Proyecto> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Buscar proyectos que empiecen en una fecha específica o después
    List<Proyecto> findByFechaInicioAfter(LocalDate fecha);
    
    // Buscar proyectos que terminen antes de una fecha específica
    List<Proyecto> findByFechaFinBefore(LocalDate fecha);
    
    // Buscar proyectos por nombre (case-insensitive, contenido)
    @Query("SELECT p FROM Proyecto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Proyecto> findByNombreContaining(@Param("nombre") String nombre);
    
    // Buscar proyectos que tengan un empleado específico
    @Query("SELECT p FROM Proyecto p JOIN p.empleados e WHERE e.id = :empleadoId")
    List<Proyecto> findByEmpleadoId(@Param("empleadoId") Long empleadoId);
    
    // Contar empleados en un proyecto
    @Query("SELECT COUNT(e) FROM Proyecto p JOIN p.empleados e WHERE p.id = :proyectoId")
    Long countEmpleadosByProyectoId(@Param("proyectoId") Long proyectoId);
    
    // Buscar proyectos sin empleados asignados
    @Query("SELECT p FROM Proyecto p WHERE SIZE(p.empleados) = 0")
    List<Proyecto> findProyectosSinEmpleados();
    
    // Buscar proyectos con más de X empleados
    @Query("SELECT p FROM Proyecto p WHERE SIZE(p.empleados) > :cantidadMinima")
    List<Proyecto> findProyectosConMasDeXEmpleados(@Param("cantidadMinima") int cantidadMinima);
}
