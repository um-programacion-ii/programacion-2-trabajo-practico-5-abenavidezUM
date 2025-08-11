package com.empleados.sistema.service.impl;

import com.empleados.sistema.exception.ProyectoNoEncontradoException;
import com.empleados.sistema.exception.EmpleadoNoEncontradoException;
import com.empleados.sistema.model.Proyecto;
import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.repository.ProyectoRepository;
import com.empleados.sistema.repository.EmpleadoRepository;
import com.empleados.sistema.service.ProyectoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProyectoServiceImpl implements ProyectoService {
    
    private final ProyectoRepository proyectoRepository;
    private final EmpleadoRepository empleadoRepository;
    
    public ProyectoServiceImpl(ProyectoRepository proyectoRepository,
                              EmpleadoRepository empleadoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.empleadoRepository = empleadoRepository;
    }
    
    @Override
    public Proyecto guardar(Proyecto proyecto) {
        // Validar que el nombre no exista
        if (proyectoRepository.existsByNombre(proyecto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un proyecto con el nombre: " + proyecto.getNombre());
        }
        
        // Validar datos básicos
        if (proyecto.getNombre() == null || proyecto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
        }
        
        // Validar fechas
        if (proyecto.getFechaInicio() != null && proyecto.getFechaFin() != null) {
            if (proyecto.getFechaInicio().isAfter(proyecto.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
            }
        }
        
        return proyectoRepository.save(proyecto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Proyecto buscarPorId(Long id) {
        return proyectoRepository.findById(id)
            .orElseThrow(() -> new ProyectoNoEncontradoException("Proyecto no encontrado con ID: " + id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }
    
    @Override
    public Proyecto actualizar(Long id, Proyecto proyecto) {
        Proyecto proyectoExistente = buscarPorId(id);
        
        // Verificar si el nombre cambió y si ya existe
        if (!proyectoExistente.getNombre().equals(proyecto.getNombre()) && 
            proyectoRepository.existsByNombre(proyecto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un proyecto con el nombre: " + proyecto.getNombre());
        }
        
        // Validar fechas
        if (proyecto.getFechaInicio() != null && proyecto.getFechaFin() != null) {
            if (proyecto.getFechaInicio().isAfter(proyecto.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
            }
        }
        
        // Actualizar campos
        proyectoExistente.setNombre(proyecto.getNombre());
        proyectoExistente.setDescripcion(proyecto.getDescripcion());
        proyectoExistente.setFechaInicio(proyecto.getFechaInicio());
        proyectoExistente.setFechaFin(proyecto.getFechaFin());
        
        return proyectoRepository.save(proyectoExistente);
    }
    
    @Override
    public void eliminar(Long id) {
        Proyecto proyecto = buscarPorId(id);
        proyectoRepository.delete(proyecto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Proyecto buscarPorNombre(String nombre) {
        return proyectoRepository.findByNombre(nombre)
            .orElseThrow(() -> new ProyectoNoEncontradoException("Proyecto no encontrado con nombre: " + nombre));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> buscarProyectosActivos() {
        return proyectoRepository.findProyectosActivos();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> buscarProyectosInactivos() {
        return proyectoRepository.findProyectosInactivos();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return proyectoRepository.findByFechaInicioBetween(fechaInicio, fechaFin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> buscarPorNombreContiene(String termino) {
        return proyectoRepository.findByNombreContaining(termino);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> obtenerEmpleadosPorProyecto(Long proyectoId) {
        Proyecto proyecto = buscarPorId(proyectoId);
        return empleadoRepository.findByProyectoId(proyectoId);
    }
    
    @Override
    public void asignarEmpleado(Long proyectoId, Long empleadoId) {
        Proyecto proyecto = buscarPorId(proyectoId);
        Empleado empleado = empleadoRepository.findById(empleadoId)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + empleadoId));
        
        proyecto.agregarEmpleado(empleado);
        proyectoRepository.save(proyecto);
    }
    
    @Override
    public void removerEmpleado(Long proyectoId, Long empleadoId) {
        Proyecto proyecto = buscarPorId(proyectoId);
        Empleado empleado = empleadoRepository.findById(empleadoId)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + empleadoId));
        
        proyecto.removerEmpleado(empleado);
        proyectoRepository.save(proyecto);
    }
    
    @Override
    public void asignarMultiplesEmpleados(Long proyectoId, List<Long> empleadoIds) {
        Proyecto proyecto = buscarPorId(proyectoId);
        
        for (Long empleadoId : empleadoIds) {
            Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + empleadoId));
            proyecto.agregarEmpleado(empleado);
        }
        
        proyectoRepository.save(proyecto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long contarEmpleados(Long proyectoId) {
        return proyectoRepository.countEmpleadosByProyectoId(proyectoId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeNombre(String nombre) {
        return proyectoRepository.existsByNombre(nombre);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean estaActivo(Long proyectoId) {
        Proyecto proyecto = buscarPorId(proyectoId);
        return proyecto.estaActivo();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerProyectosSinEmpleados() {
        return proyectoRepository.findProyectosSinEmpleados();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerProyectosConMasDeXEmpleados(int cantidadMinima) {
        return proyectoRepository.findProyectosConMasDeXEmpleados(cantidadMinima);
    }
    
    @Override
    public void finalizarProyecto(Long proyectoId) {
        Proyecto proyecto = buscarPorId(proyectoId);
        proyecto.setFechaFin(LocalDate.now());
        proyectoRepository.save(proyecto);
    }
}
