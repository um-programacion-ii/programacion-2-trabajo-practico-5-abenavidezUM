package com.empleados.sistema.controller;

import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    
    private final EmpleadoService empleadoService;
    
    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }
    
    // Operaciones CRUD básicas
    
    @GetMapping
    public ResponseEntity<List<Empleado>> obtenerTodos() {
        List<Empleado> empleados = empleadoService.obtenerTodos();
        return ResponseEntity.ok(empleados);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtenerPorId(@PathVariable Long id) {
        Empleado empleado = empleadoService.buscarPorId(id);
        return ResponseEntity.ok(empleado);
    }
    
    @PostMapping
    public ResponseEntity<Empleado> crear(@Valid @RequestBody Empleado empleado) {
        Empleado nuevoEmpleado = empleadoService.guardar(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizar(@PathVariable Long id, @Valid @RequestBody Empleado empleado) {
        Empleado empleadoActualizado = empleadoService.actualizar(id, empleado);
        return ResponseEntity.ok(empleadoActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    // Búsquedas específicas
    
    @GetMapping("/email/{email}")
    public ResponseEntity<Empleado> buscarPorEmail(@PathVariable String email) {
        Empleado empleado = empleadoService.buscarPorEmail(email);
        return ResponseEntity.ok(empleado);
    }
    
    @GetMapping("/departamento/{nombreDepartamento}")
    public ResponseEntity<List<Empleado>> buscarPorDepartamento(@PathVariable String nombreDepartamento) {
        List<Empleado> empleados = empleadoService.buscarPorDepartamento(nombreDepartamento);
        return ResponseEntity.ok(empleados);
    }
    
    @GetMapping("/salario")
    public ResponseEntity<List<Empleado>> buscarPorRangoSalario(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        List<Empleado> empleados = empleadoService.buscarPorRangoSalario(min, max);
        return ResponseEntity.ok(empleados);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Empleado>> buscarPorNombreOApellido(@RequestParam String termino) {
        List<Empleado> empleados = empleadoService.buscarPorNombreOApellido(termino);
        return ResponseEntity.ok(empleados);
    }
    
    @GetMapping("/sin-departamento")
    public ResponseEntity<List<Empleado>> obtenerEmpleadosSinDepartamento() {
        List<Empleado> empleados = empleadoService.obtenerEmpleadosSinDepartamento();
        return ResponseEntity.ok(empleados);
    }
    
    // Operaciones de negocio
    
    @PutMapping("/{empleadoId}/departamento/{departamentoId}")
    public ResponseEntity<Void> asignarADepartamento(
            @PathVariable Long empleadoId, 
            @PathVariable Long departamentoId) {
        empleadoService.asignarADepartamento(empleadoId, departamentoId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{empleadoId}/proyecto/{proyectoId}")
    public ResponseEntity<Void> asignarAProyecto(
            @PathVariable Long empleadoId, 
            @PathVariable Long proyectoId) {
        empleadoService.asignarAProyecto(empleadoId, proyectoId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{empleadoId}/proyecto/{proyectoId}")
    public ResponseEntity<Void> removerDeProyecto(
            @PathVariable Long empleadoId, 
            @PathVariable Long proyectoId) {
        empleadoService.removerDeProyecto(empleadoId, proyectoId);
        return ResponseEntity.ok().build();
    }
    
    // Reportes y estadísticas
    
    @GetMapping("/departamento/{departamentoId}/salario-promedio")
    public ResponseEntity<BigDecimal> obtenerSalarioPromedio(@PathVariable Long departamentoId) {
        BigDecimal salarioPromedio = empleadoService.obtenerSalarioPromedioPorDepartamento(departamentoId);
        return ResponseEntity.ok(salarioPromedio);
    }
    
    @GetMapping("/departamento/{departamentoId}/cantidad")
    public ResponseEntity<Long> contarEmpleadosPorDepartamento(@PathVariable Long departamentoId) {
        Long cantidad = empleadoService.contarEmpleadosPorDepartamento(departamentoId);
        return ResponseEntity.ok(cantidad);
    }
}
