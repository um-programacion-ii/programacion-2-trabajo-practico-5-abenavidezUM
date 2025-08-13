package com.empleados.sistema.controller;

import com.empleados.sistema.model.Proyecto;
import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.service.ProyectoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {
    
    private final ProyectoService proyectoService;
    
    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }
    
    // Operaciones CRUD básicas
    
    @GetMapping
    public ResponseEntity<List<Proyecto>> obtenerTodos() {
        List<Proyecto> proyectos = proyectoService.obtenerTodos();
        return ResponseEntity.ok(proyectos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtenerPorId(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.buscarPorId(id);
        return ResponseEntity.ok(proyecto);
    }
    
    @PostMapping
    public ResponseEntity<Proyecto> crear(@RequestBody Proyecto proyecto) {
        Proyecto nuevoProyecto = proyectoService.guardar(proyecto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProyecto);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(@PathVariable Long id, @RequestBody Proyecto proyecto) {
        Proyecto proyectoActualizado = proyectoService.actualizar(id, proyecto);
        return ResponseEntity.ok(proyectoActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    // Búsquedas específicas
    
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Proyecto> buscarPorNombre(@PathVariable String nombre) {
        Proyecto proyecto = proyectoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(proyecto);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Proyecto>> buscarProyectosActivos() {
        List<Proyecto> proyectos = proyectoService.buscarProyectosActivos();
        return ResponseEntity.ok(proyectos);
    }
    
    @GetMapping("/inactivos")
    public ResponseEntity<List<Proyecto>> buscarProyectosInactivos() {
        List<Proyecto> proyectos = proyectoService.buscarProyectosInactivos();
        return ResponseEntity.ok(proyectos);
    }
    
    @GetMapping("/por-fechas")
    public ResponseEntity<List<Proyecto>> buscarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Proyecto> proyectos = proyectoService.buscarPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(proyectos);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Proyecto>> buscarPorNombreContiene(@RequestParam String termino) {
        List<Proyecto> proyectos = proyectoService.buscarPorNombreContiene(termino);
        return ResponseEntity.ok(proyectos);
    }
    
    @GetMapping("/sin-empleados")
    public ResponseEntity<List<Proyecto>> obtenerProyectosSinEmpleados() {
        List<Proyecto> proyectos = proyectoService.obtenerProyectosSinEmpleados();
        return ResponseEntity.ok(proyectos);
    }
    
    @GetMapping("/con-mas-empleados")
    public ResponseEntity<List<Proyecto>> obtenerProyectosConMasEmpleados(@RequestParam int cantidadMinima) {
        List<Proyecto> proyectos = proyectoService.obtenerProyectosConMasDeXEmpleados(cantidadMinima);
        return ResponseEntity.ok(proyectos);
    }
    
    // Gestión de empleados
    
    @GetMapping("/{id}/empleados")
    public ResponseEntity<List<Empleado>> obtenerEmpleados(@PathVariable Long id) {
        List<Empleado> empleados = proyectoService.obtenerEmpleadosPorProyecto(id);
        return ResponseEntity.ok(empleados);
    }
    
    @PutMapping("/{proyectoId}/empleados/{empleadoId}")
    public ResponseEntity<Void> asignarEmpleado(
            @PathVariable Long proyectoId, 
            @PathVariable Long empleadoId) {
        proyectoService.asignarEmpleado(proyectoId, empleadoId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{proyectoId}/empleados/{empleadoId}")
    public ResponseEntity<Void> removerEmpleado(
            @PathVariable Long proyectoId, 
            @PathVariable Long empleadoId) {
        proyectoService.removerEmpleado(proyectoId, empleadoId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{proyectoId}/empleados")
    public ResponseEntity<Void> asignarMultiplesEmpleados(
            @PathVariable Long proyectoId, 
            @RequestBody List<Long> empleadoIds) {
        proyectoService.asignarMultiplesEmpleados(proyectoId, empleadoIds);
        return ResponseEntity.ok().build();
    }
    
    // Reportes y estadísticas
    
    @GetMapping("/{id}/cantidad-empleados")
    public ResponseEntity<Long> contarEmpleados(@PathVariable Long id) {
        Long cantidad = proyectoService.contarEmpleados(id);
        return ResponseEntity.ok(cantidad);
    }
    
    // Validaciones y utilidades
    
    @GetMapping("/exists/nombre/{nombre}")
    public ResponseEntity<Boolean> existeNombre(@PathVariable String nombre) {
        boolean existe = proyectoService.existeNombre(nombre);
        return ResponseEntity.ok(existe);
    }
    
    @GetMapping("/{id}/activo")
    public ResponseEntity<Boolean> estaActivo(@PathVariable Long id) {
        boolean activo = proyectoService.estaActivo(id);
        return ResponseEntity.ok(activo);
    }
    
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizarProyecto(@PathVariable Long id) {
        proyectoService.finalizarProyecto(id);
        return ResponseEntity.ok().build();
    }
}
