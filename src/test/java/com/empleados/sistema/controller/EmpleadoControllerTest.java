package com.empleados.sistema.controller;

import com.empleados.sistema.exception.EmpleadoNoEncontradoException;
import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmpleadoController.class)
public class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmpleadoService empleadoService;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setEmail("juan.perez@empresa.com");
        empleado.setFechaContratacion(LocalDate.now().minusYears(1));
        empleado.setSalario(new BigDecimal("50000.00"));
    }

    @Test
    void debeObtenerTodosLosEmpleados() throws Exception {
        // Arrange
        Empleado otroEmpleado = new Empleado();
        otroEmpleado.setId(2L);
        otroEmpleado.setNombre("Ana");
        otroEmpleado.setApellido("Gómez");
        otroEmpleado.setEmail("ana.gomez@empresa.com");
        otroEmpleado.setFechaContratacion(LocalDate.now().minusMonths(6));
        otroEmpleado.setSalario(new BigDecimal("45000.00"));

        List<Empleado> empleados = Arrays.asList(empleado, otroEmpleado);
        when(empleadoService.obtenerTodos()).thenReturn(empleados);

        // Act & Assert
        mockMvc.perform(get("/api/empleados"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nombre", is("Juan")))
            .andExpect(jsonPath("$[1].nombre", is("Ana")));

        verify(empleadoService, times(1)).obtenerTodos();
    }

    @Test
    void debeObtenerEmpleadoPorId() throws Exception {
        // Arrange
        when(empleadoService.buscarPorId(1L)).thenReturn(empleado);

        // Act & Assert
        mockMvc.perform(get("/api/empleados/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombre", is("Juan")))
            .andExpect(jsonPath("$.apellido", is("Pérez")))
            .andExpect(jsonPath("$.email", is("juan.perez@empresa.com")));

        verify(empleadoService, times(1)).buscarPorId(1L);
    }

    @Test
    void debeLanzarExcepcionCuandoEmpleadoNoExiste() throws Exception {
        // Arrange
        when(empleadoService.buscarPorId(99L)).thenThrow(new EmpleadoNoEncontradoException("Empleado no encontrado con ID: 99"));

        // Act & Assert
        mockMvc.perform(get("/api/empleados/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Empleado no encontrado")))
            .andExpect(jsonPath("$.message", containsString("99")));

        verify(empleadoService, times(1)).buscarPorId(99L);
    }

    @Test
    void debeCrearEmpleado() throws Exception {
        // Arrange
        when(empleadoService.guardar(any(Empleado.class))).thenReturn(empleado);

        // Act & Assert
        mockMvc.perform(post("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleado)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombre", is("Juan")))
            .andExpect(jsonPath("$.email", is("juan.perez@empresa.com")));

        verify(empleadoService, times(1)).guardar(any(Empleado.class));
    }

    @Test
    void debeActualizarEmpleado() throws Exception {
        // Arrange
        Empleado empleadoActualizado = new Empleado();
        empleadoActualizado.setId(1L);
        empleadoActualizado.setNombre("Juan Carlos");
        empleadoActualizado.setApellido("Pérez");
        empleadoActualizado.setEmail("juan.perez@empresa.com");
        empleadoActualizado.setFechaContratacion(LocalDate.now().minusYears(1));
        empleadoActualizado.setSalario(new BigDecimal("55000.00"));

        when(empleadoService.actualizar(eq(1L), any(Empleado.class))).thenReturn(empleadoActualizado);

        // Act & Assert
        mockMvc.perform(put("/api/empleados/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleadoActualizado)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombre", is("Juan Carlos")))
            .andExpect(jsonPath("$.salario", is(55000.00)));

        verify(empleadoService, times(1)).actualizar(eq(1L), any(Empleado.class));
    }

    @Test
    void debeEliminarEmpleado() throws Exception {
        // Arrange
        doNothing().when(empleadoService).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/empleados/1"))
            .andExpect(status().isNoContent());

        verify(empleadoService, times(1)).eliminar(1L);
    }

    @Test
    void debeBuscarEmpleadosPorDepartamento() throws Exception {
        // Arrange
        List<Empleado> empleados = Arrays.asList(empleado);
        when(empleadoService.buscarPorDepartamento("IT")).thenReturn(empleados);

        // Act & Assert
        mockMvc.perform(get("/api/empleados/departamento/IT"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nombre", is("Juan")));

        verify(empleadoService, times(1)).buscarPorDepartamento("IT");
    }
}
