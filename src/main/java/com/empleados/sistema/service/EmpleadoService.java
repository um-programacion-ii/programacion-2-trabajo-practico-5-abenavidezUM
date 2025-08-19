package com.empleados.sistema.service;

import com.empleados.sistema.model.Empleado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface EmpleadoService {
    
    // Operaciones básicas CRUD
    Empleado guardar(Empleado empleado);
    Empleado buscarPorId(Long id);
    List<Empleado> obtenerTodos();
    Empleado actualizar(Long id, Empleado empleado);
    void eliminar(Long id);
    
    // Búsquedas específicas
    Empleado buscarPorEmail(String email);
    List<Empleado> buscarPorDepartamento(String nombreDepartamento);
    List<Empleado> buscarPorRangoSalario(BigDecimal salarioMin, BigDecimal salarioMax);
    List<Empleado> buscarPorFechaContratacion(LocalDate fechaDesde, LocalDate fechaHasta);
    List<Empleado> buscarPorNombreOApellido(String termino);
    
    // Operaciones de negocio
    BigDecimal obtenerSalarioPromedioPorDepartamento(Long departamentoId);
    void asignarADepartamento(Long empleadoId, Long departamentoId);
    void asignarAProyecto(Long empleadoId, Long proyectoId);
    void removerDeProyecto(Long empleadoId, Long proyectoId);
    
    // Validaciones
    boolean existeEmail(String email);
    List<Empleado> obtenerEmpleadosSinDepartamento();
    Long contarEmpleadosPorDepartamento(Long departamentoId);
}
