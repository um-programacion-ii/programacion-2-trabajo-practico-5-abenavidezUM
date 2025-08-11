package com.empleados.sistema.service.impl;

import com.empleados.sistema.exception.DepartamentoNoEncontradoException;
import com.empleados.sistema.exception.EmpleadoNoEncontradoException;
import com.empleados.sistema.model.Departamento;
import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.repository.DepartamentoRepository;
import com.empleados.sistema.repository.EmpleadoRepository;
import com.empleados.sistema.service.DepartamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class DepartamentoServiceImpl implements DepartamentoService {
    
    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;
    
    public DepartamentoServiceImpl(DepartamentoRepository departamentoRepository,
                                  EmpleadoRepository empleadoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
    }
    
    @Override
    public Departamento guardar(Departamento departamento) {
        // Validar que el nombre no exista
        if (departamentoRepository.existsByNombre(departamento.getNombre())) {
            throw new IllegalArgumentException("Ya existe un departamento con el nombre: " + departamento.getNombre());
        }
        
        // Validar datos básicos
        if (departamento.getNombre() == null || departamento.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio");
        }
        
        return departamentoRepository.save(departamento);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Departamento buscarPorId(Long id) {
        return departamentoRepository.findById(id)
            .orElseThrow(() -> new DepartamentoNoEncontradoException("Departamento no encontrado con ID: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Departamento> obtenerTodos() {
        return departamentoRepository.findAll();
    }
    
    @Override
    public Departamento actualizar(Long id, Departamento departamento) {
        Departamento departamentoExistente = buscarPorId(id);
        
        // Verificar si el nombre cambió y si ya existe
        if (!departamentoExistente.getNombre().equals(departamento.getNombre()) && 
            departamentoRepository.existsByNombre(departamento.getNombre())) {
            throw new IllegalArgumentException("Ya existe un departamento con el nombre: " + departamento.getNombre());
        }
        
        // Actualizar campos
        departamentoExistente.setNombre(departamento.getNombre());
        departamentoExistente.setDescripcion(departamento.getDescripcion());
        
        return departamentoRepository.save(departamentoExistente);
    }
    
    @Override
    public void eliminar(Long id) {
        Departamento departamento = buscarPorId(id);
        
        // Verificar si tiene empleados asignados
        if (!puedeEliminar(id)) {
            throw new IllegalStateException("No se puede eliminar el departamento porque tiene empleados asignados");
        }
        
        departamentoRepository.delete(departamento);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Departamento buscarPorNombre(String nombre) {
        return departamentoRepository.findByNombre(nombre)
            .orElseThrow(() -> new DepartamentoNoEncontradoException("Departamento no encontrado con nombre: " + nombre));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Departamento> buscarPorSalarioMinimo(BigDecimal salarioMinimo) {
        return departamentoRepository.findDepartamentosConSalarioMayorA(salarioMinimo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularSalarioPromedio(Long departamentoId) {
        return departamentoRepository.findSalarioPromedioPorDepartamento(departamentoId)
            .orElse(BigDecimal.ZERO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> obtenerEmpleadosPorDepartamento(Long departamentoId) {
        Departamento departamento = buscarPorId(departamentoId);
        return empleadoRepository.findByDepartamento(departamento);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long contarEmpleados(Long departamentoId) {
        return departamentoRepository.countEmpleadosByDepartamentoId(departamentoId);
    }
    
    @Override
    public void agregarEmpleado(Long departamentoId, Long empleadoId) {
        Departamento departamento = buscarPorId(departamentoId);
        Empleado empleado = empleadoRepository.findById(empleadoId)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + empleadoId));
        
        empleado.setDepartamento(departamento);
        empleadoRepository.save(empleado);
    }
    
    @Override
    public void removerEmpleado(Long departamentoId, Long empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + empleadoId));
        
        // Verificar que el empleado pertenezca al departamento
        if (empleado.getDepartamento() == null || !empleado.getDepartamento().getId().equals(departamentoId)) {
            throw new IllegalArgumentException("El empleado no pertenece al departamento especificado");
        }
        
        empleado.setDepartamento(null);
        empleadoRepository.save(empleado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeNombre(String nombre) {
        return departamentoRepository.existsByNombre(nombre);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean puedeEliminar(Long departamentoId) {
        Long cantidadEmpleados = contarEmpleados(departamentoId);
        return cantidadEmpleados == 0;
    }
}
