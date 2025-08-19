package com.empleados.sistema.service.impl;

import com.empleados.sistema.exception.EmpleadoNoEncontradoException;
import com.empleados.sistema.exception.EmailDuplicadoException;
import com.empleados.sistema.exception.DepartamentoNoEncontradoException;
import com.empleados.sistema.exception.ProyectoNoEncontradoException;
import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.model.Departamento;
import com.empleados.sistema.model.Proyecto;
import com.empleados.sistema.repository.EmpleadoRepository;
import com.empleados.sistema.repository.DepartamentoRepository;
import com.empleados.sistema.repository.ProyectoRepository;
import com.empleados.sistema.service.EmpleadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {
    
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ProyectoRepository proyectoRepository;
    
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, 
                              DepartamentoRepository departamentoRepository,
                              ProyectoRepository proyectoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
        this.proyectoRepository = proyectoRepository;
    }
    
    @Override
    public Empleado guardar(Empleado empleado) {
        // Validar que el email no exista
        if (empleadoRepository.existsByEmail(empleado.getEmail())) {
            throw new EmailDuplicadoException("Ya existe un empleado con el email: " + empleado.getEmail());
        }
        
        // Validar datos básicos
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del empleado es obligatorio");
        }
        
        if (empleado.getApellido() == null || empleado.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del empleado es obligatorio");
        }
        
        if (empleado.getSalario() == null || empleado.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El salario debe ser mayor a cero");
        }
        
        return empleadoRepository.save(empleado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Empleado buscarPorId(Long id) {
        return empleadoRepository.findById(id)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }
    
    @Override
    public Empleado actualizar(Long id, Empleado empleado) {
        Empleado empleadoExistente = buscarPorId(id);
        
        // Verificar si el email cambió y si ya existe
        if (!empleadoExistente.getEmail().equals(empleado.getEmail()) && 
            empleadoRepository.existsByEmail(empleado.getEmail())) {
            throw new EmailDuplicadoException("Ya existe un empleado con el email: " + empleado.getEmail());
        }
        
        // Actualizar campos
        empleadoExistente.setNombre(empleado.getNombre());
        empleadoExistente.setApellido(empleado.getApellido());
        empleadoExistente.setEmail(empleado.getEmail());
        empleadoExistente.setSalario(empleado.getSalario());
        empleadoExistente.setFechaContratacion(empleado.getFechaContratacion());
        
        if (empleado.getDepartamento() != null) {
            empleadoExistente.setDepartamento(empleado.getDepartamento());
        }
        
        return empleadoRepository.save(empleadoExistente);
    }
    
    @Override
    public void eliminar(Long id) {
        Empleado empleado = buscarPorId(id);
        empleadoRepository.delete(empleado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Empleado buscarPorEmail(String email) {
        return empleadoRepository.findByEmail(email)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con email: " + email));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> buscarPorDepartamento(String nombreDepartamento) {
        return empleadoRepository.findByNombreDepartamento(nombreDepartamento);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> buscarPorRangoSalario(BigDecimal salarioMin, BigDecimal salarioMax) {
        if (salarioMin.compareTo(salarioMax) > 0) {
            throw new IllegalArgumentException("El salario mínimo no puede ser mayor al máximo");
        }
        return empleadoRepository.findBySalarioBetween(salarioMin, salarioMax);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> buscarPorFechaContratacion(LocalDate fechaDesde, LocalDate fechaHasta) {
        // Buscar empleados contratados después de fechaDesde
        return empleadoRepository.findByFechaContratacionAfter(fechaDesde);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> buscarPorNombreOApellido(String termino) {
        return empleadoRepository.findByNombreOrApellidoContaining(termino);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenerSalarioPromedioPorDepartamento(Long departamentoId) {
        return empleadoRepository.findAverageSalarioByDepartamento(departamentoId)
            .orElse(BigDecimal.ZERO);
    }
    
    @Override
    public void asignarADepartamento(Long empleadoId, Long departamentoId) {
        Empleado empleado = buscarPorId(empleadoId);
        Departamento departamento = departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new DepartamentoNoEncontradoException("Departamento no encontrado con ID: " + departamentoId));
        
        empleado.setDepartamento(departamento);
        empleadoRepository.save(empleado);
    }
    
    @Override
    public void asignarAProyecto(Long empleadoId, Long proyectoId) {
        Empleado empleado = buscarPorId(empleadoId);
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
            .orElseThrow(() -> new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + proyectoId));
        
        empleado.asignarAProyecto(proyecto);
        empleadoRepository.save(empleado);
    }
    
    @Override
    public void removerDeProyecto(Long empleadoId, Long proyectoId) {
        Empleado empleado = buscarPorId(empleadoId);
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
            .orElseThrow(() -> new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + proyectoId));
        
        empleado.removerDeProyecto(proyecto);
        empleadoRepository.save(empleado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return empleadoRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> obtenerEmpleadosSinDepartamento() {
        return empleadoRepository.findByDepartamentoIsNull();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long contarEmpleadosPorDepartamento(Long departamentoId) {
        return empleadoRepository.countByDepartamentoId(departamentoId);
    }
}
