package com.empleados.sistema.repository;

import com.empleados.sistema.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    
    // Método derivado para buscar por nombre
    Optional<Departamento> findByNombre(String nombre);
    
    // Verificar si existe un departamento con ese nombre
    boolean existsByNombre(String nombre);
    
    // Consulta personalizada para obtener el salario promedio del departamento
    @Query("SELECT AVG(e.salario) FROM Empleado e WHERE e.departamento.id = :departamentoId")
    Optional<BigDecimal> findSalarioPromedioPorDepartamento(@Param("departamentoId") Long departamentoId);
    
    // Consulta para contar empleados por departamento
    @Query("SELECT COUNT(e) FROM Empleado e WHERE e.departamento.id = :departamentoId")
    Long countEmpleadosByDepartamentoId(@Param("departamentoId") Long departamentoId);
    
    // Buscar departamentos que tengan empleados con salario mayor a X
    @Query("SELECT DISTINCT d FROM Departamento d JOIN d.empleados e WHERE e.salario > :salarioMinimo")
    java.util.List<Departamento> findDepartamentosConSalarioMayorA(@Param("salarioMinimo") BigDecimal salarioMinimo);
}
