package com.empleados.sistema.repository;

import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    // Buscar empleado por email (único)
    Optional<Empleado> findByEmail(String email);
    
    // Verificar si existe un empleado con ese email
    boolean existsByEmail(String email);
    
    // Buscar empleados por departamento
    List<Empleado> findByDepartamento(Departamento departamento);
    
    // Buscar empleados por nombre del departamento
    @Query("SELECT e FROM Empleado e WHERE e.departamento.nombre = :nombreDepartamento")
    List<Empleado> findByNombreDepartamento(@Param("nombreDepartamento") String nombreDepartamento);
    
    // Buscar empleados por rango de salario
    List<Empleado> findBySalarioBetween(BigDecimal salarioMin, BigDecimal salarioMax);
    
    // Buscar empleados contratados después de una fecha
    List<Empleado> findByFechaContratacionAfter(LocalDate fecha);
    
    // Buscar empleados contratados en un año específico
    @Query("SELECT e FROM Empleado e WHERE YEAR(e.fechaContratacion) = :anio")
    List<Empleado> findByAnioContratacion(@Param("anio") int anio);
    
    // Buscar empleados por nombre o apellido (case-insensitive)
    @Query("SELECT e FROM Empleado e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(e.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Empleado> findByNombreOrApellidoContaining(@Param("termino") String termino);
    
    // Obtener salario promedio por departamento
    @Query("SELECT AVG(e.salario) FROM Empleado e WHERE e.departamento.id = :departamentoId")
    Optional<BigDecimal> findAverageSalarioByDepartamento(@Param("departamentoId") Long departamentoId);
    
    // Buscar empleados sin departamento asignado
    List<Empleado> findByDepartamentoIsNull();
    
    // Buscar empleados que trabajen en un proyecto específico
    @Query("SELECT e FROM Empleado e JOIN e.proyectos p WHERE p.id = :proyectoId")
    List<Empleado> findByProyectoId(@Param("proyectoId") Long proyectoId);
    
    // Contar empleados por departamento
    @Query("SELECT COUNT(e) FROM Empleado e WHERE e.departamento.id = :departamentoId")
    Long countByDepartamentoId(@Param("departamentoId") Long departamentoId);
}
