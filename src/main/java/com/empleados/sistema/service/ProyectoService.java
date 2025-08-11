package com.empleados.sistema.service;

import com.empleados.sistema.model.Proyecto;
import com.empleados.sistema.model.Empleado;

import java.time.LocalDate;
import java.util.List;

public interface ProyectoService {
    
    // Operaciones básicas CRUD
    Proyecto guardar(Proyecto proyecto);
    Proyecto buscarPorId(Long id);
    List<Proyecto> obtenerTodos();
    Proyecto actualizar(Long id, Proyecto proyecto);
    void eliminar(Long id);
    
    // Búsquedas específicas
    Proyecto buscarPorNombre(String nombre);
    List<Proyecto> buscarProyectosActivos();
    List<Proyecto> buscarProyectosInactivos();
    List<Proyecto> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    List<Proyecto> buscarPorNombreContiene(String termino);
    
    // Operaciones de negocio
    List<Empleado> obtenerEmpleadosPorProyecto(Long proyectoId);
    void asignarEmpleado(Long proyectoId, Long empleadoId);
    void removerEmpleado(Long proyectoId, Long empleadoId);
    void asignarMultiplesEmpleados(Long proyectoId, List<Long> empleadoIds);
    Long contarEmpleados(Long proyectoId);
    
    // Validaciones y utilidades
    boolean existeNombre(String nombre);
    boolean estaActivo(Long proyectoId);
    List<Proyecto> obtenerProyectosSinEmpleados();
    List<Proyecto> obtenerProyectosConMasDeXEmpleados(int cantidadMinima);
    void finalizarProyecto(Long proyectoId);
}
