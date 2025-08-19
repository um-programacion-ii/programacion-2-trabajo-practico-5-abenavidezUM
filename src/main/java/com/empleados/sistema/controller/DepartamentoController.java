package com.empleados.sistema.controller;

import com.empleados.sistema.model.Departamento;
import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.service.DepartamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {
    
    private final DepartamentoService departamentoService;
    
    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }
    
    // Operaciones CRUD básicas
    
    @GetMapping
    public ResponseEntity<List<Departamento>> obtenerTodos() {
        List<Departamento> departamentos = departamentoService.obtenerTodos();
        return ResponseEntity.ok(departamentos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Departamento> obtenerPorId(@PathVariable Long id) {
        Departamento departamento = departamentoService.buscarPorId(id);
        return ResponseEntity.ok(departamento);
    }
    
    @PostMapping
    public ResponseEntity<Departamento> crear(@RequestBody Departamento departamento) {
        Departamento nuevoDepartamento = departamentoService.guardar(departamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDepartamento);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Departamento> actualizar(@PathVariable Long id, @RequestBody Departamento departamento) {
        Departamento departamentoActualizado = departamentoService.actualizar(id, departamento);
        return ResponseEntity.ok(departamentoActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        departamentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    // Búsquedas específicas
    
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Departamento> buscarPorNombre(@PathVariable String nombre) {
        Departamento departamento = departamentoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(departamento);
    }
    
    @GetMapping("/salario-minimo")
    public ResponseEntity<List<Departamento>> buscarPorSalarioMinimo(@RequestParam BigDecimal salarioMinimo) {
        List<Departamento> departamentos = departamentoService.buscarPorSalarioMinimo(salarioMinimo);
        return ResponseEntity.ok(departamentos);
    }
    
    // Gestión de empleados
    
    @GetMapping("/{id}/empleados")
    public ResponseEntity<List<Empleado>> obtenerEmpleados(@PathVariable Long id) {
        List<Empleado> empleados = departamentoService.obtenerEmpleadosPorDepartamento(id);
        return ResponseEntity.ok(empleados);
    }
    
    @PutMapping("/{departamentoId}/empleados/{empleadoId}")
    public ResponseEntity<Void> agregarEmpleado(
            @PathVariable Long departamentoId, 
            @PathVariable Long empleadoId) {
        departamentoService.agregarEmpleado(departamentoId, empleadoId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{departamentoId}/empleados/{empleadoId}")
    public ResponseEntity<Void> removerEmpleado(
            @PathVariable Long departamentoId, 
            @PathVariable Long empleadoId) {
        departamentoService.removerEmpleado(departamentoId, empleadoId);
        return ResponseEntity.ok().build();
    }
    
    // Reportes y estadísticas
    
    @GetMapping("/{id}/salario-promedio")
    public ResponseEntity<BigDecimal> calcularSalarioPromedio(@PathVariable Long id) {
        BigDecimal salarioPromedio = departamentoService.calcularSalarioPromedio(id);
        return ResponseEntity.ok(salarioPromedio);
    }
    
    @GetMapping("/{id}/cantidad-empleados")
    public ResponseEntity<Long> contarEmpleados(@PathVariable Long id) {
        Long cantidad = departamentoService.contarEmpleados(id);
        return ResponseEntity.ok(cantidad);
    }
    
    // Validaciones
    
    @GetMapping("/exists/nombre/{nombre}")
    public ResponseEntity<Boolean> existeNombre(@PathVariable String nombre) {
        boolean existe = departamentoService.existeNombre(nombre);
        return ResponseEntity.ok(existe);
    }
    
    @GetMapping("/{id}/puede-eliminar")
    public ResponseEntity<Boolean> puedeEliminar(@PathVariable Long id) {
        boolean puede = departamentoService.puedeEliminar(id);
        return ResponseEntity.ok(puede);
    }
}
