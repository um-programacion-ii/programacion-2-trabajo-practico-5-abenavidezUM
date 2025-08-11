package com.empleados.sistema.service;

import com.empleados.sistema.model.Departamento;
import com.empleados.sistema.model.Empleado;

import java.math.BigDecimal;
import java.util.List;

public interface DepartamentoService {
    
    // Operaciones básicas CRUD
    Departamento guardar(Departamento departamento);
    Departamento buscarPorId(Long id);
    List<Departamento> obtenerTodos();
    Departamento actualizar(Long id, Departamento departamento);
    void eliminar(Long id);
    
    // Búsquedas específicas
    Departamento buscarPorNombre(String nombre);
    List<Departamento> buscarPorSalarioMinimo(BigDecimal salarioMinimo);
    
    // Operaciones de negocio
    BigDecimal calcularSalarioPromedio(Long departamentoId);
    List<Empleado> obtenerEmpleadosPorDepartamento(Long departamentoId);
    Long contarEmpleados(Long departamentoId);
    void agregarEmpleado(Long departamentoId, Long empleadoId);
    void removerEmpleado(Long departamentoId, Long empleadoId);
    
    // Validaciones
    boolean existeNombre(String nombre);
    boolean puedeEliminar(Long departamentoId);
}
