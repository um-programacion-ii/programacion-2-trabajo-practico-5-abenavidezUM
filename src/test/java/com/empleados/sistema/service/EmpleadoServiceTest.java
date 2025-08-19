package com.empleados.sistema.service;

import com.empleados.sistema.exception.EmailDuplicadoException;
import com.empleados.sistema.exception.EmpleadoNoEncontradoException;
import com.empleados.sistema.model.Departamento;
import com.empleados.sistema.model.Empleado;
import com.empleados.sistema.repository.DepartamentoRepository;
import com.empleados.sistema.repository.EmpleadoRepository;
import com.empleados.sistema.repository.ProyectoRepository;
import com.empleados.sistema.service.impl.EmpleadoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private ProyectoRepository proyectoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    private Empleado empleado;
    private Departamento departamento;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        departamento = new Departamento();
        departamento.setId(1L);
        departamento.setNombre("IT");
        departamento.setDescripcion("Departamento de Tecnología");

        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setEmail("juan.perez@empresa.com");
        empleado.setFechaContratacion(LocalDate.now().minusYears(1));
        empleado.setSalario(new BigDecimal("50000.00"));
        empleado.setDepartamento(departamento);
    }

    @Test
    void debeGuardarEmpleadoCorrectamente() {
        // Arrange
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        Empleado resultado = empleadoService.guardar(empleado);

        // Assert
        assertNotNull(resultado);
        assertEquals("juan.perez@empresa.com", resultado.getEmail());
        verify(empleadoRepository, times(1)).existsByEmail("juan.perez@empresa.com");
        verify(empleadoRepository, times(1)).save(empleado);
    }

    @Test
    void debeLanzarExcepcionCuandoEmailYaExiste() {
        // Arrange
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        EmailDuplicadoException exception = assertThrows(
            EmailDuplicadoException.class,
            () -> empleadoService.guardar(empleado)
        );

        assertTrue(exception.getMessage().contains("juan.perez@empresa.com"));
        verify(empleadoRepository, times(1)).existsByEmail("juan.perez@empresa.com");
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    @Test
    void debeBuscarEmpleadoPorId() {
        // Arrange
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.of(empleado));

        // Act
        Empleado resultado = empleadoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan", resultado.getNombre());
        verify(empleadoRepository, times(1)).findById(1L);
    }

    @Test
    void debeLanzarExcepcionCuandoEmpleadoNoExiste() {
        // Arrange
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        EmpleadoNoEncontradoException exception = assertThrows(
            EmpleadoNoEncontradoException.class,
            () -> empleadoService.buscarPorId(1L)
        );

        assertTrue(exception.getMessage().contains("1"));
        verify(empleadoRepository, times(1)).findById(1L);
    }

    @Test
    void debeObtenerTodosLosEmpleados() {
        // Arrange
        Empleado otroEmpleado = new Empleado();
        otroEmpleado.setId(2L);
        otroEmpleado.setNombre("Ana");
        otroEmpleado.setApellido("Gómez");
        otroEmpleado.setEmail("ana.gomez@empresa.com");
        otroEmpleado.setFechaContratacion(LocalDate.now().minusMonths(6));
        otroEmpleado.setSalario(new BigDecimal("45000.00"));

        List<Empleado> empleados = Arrays.asList(empleado, otroEmpleado);
        when(empleadoRepository.findAll()).thenReturn(empleados);

        // Act
        List<Empleado> resultado = empleadoService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("Ana", resultado.get(1).getNombre());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void debeActualizarEmpleadoCorrectamente() {
        // Arrange
        Empleado empleadoActualizado = new Empleado();
        empleadoActualizado.setId(1L);
        empleadoActualizado.setNombre("Juan Carlos");
        empleadoActualizado.setApellido("Pérez");
        empleadoActualizado.setEmail("juan.perez@empresa.com");
        empleadoActualizado.setFechaContratacion(LocalDate.now().minusYears(1));
        empleadoActualizado.setSalario(new BigDecimal("55000.00"));

        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoActualizado);

        // Act
        Empleado resultado = empleadoService.actualizar(1L, empleadoActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan Carlos", resultado.getNombre());
        assertEquals(new BigDecimal("55000.00"), resultado.getSalario());
        verify(empleadoRepository, times(1)).findById(1L);
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    @Test
    void debeEliminarEmpleado() {
        // Arrange
        when(empleadoRepository.findById(anyLong())).thenReturn(Optional.of(empleado));
        doNothing().when(empleadoRepository).delete(any(Empleado.class));

        // Act
        empleadoService.eliminar(1L);

        // Assert
        verify(empleadoRepository, times(1)).findById(1L);
        verify(empleadoRepository, times(1)).delete(empleado);
    }

    @Test
    void debeBuscarEmpleadosPorDepartamento() {
        // Arrange
        List<Empleado> empleadosDepartamento = Arrays.asList(empleado);
        when(empleadoRepository.findByNombreDepartamento(anyString())).thenReturn(empleadosDepartamento);

        // Act
        List<Empleado> resultado = empleadoService.buscarPorDepartamento("IT");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("IT", resultado.get(0).getDepartamento().getNombre());
        verify(empleadoRepository, times(1)).findByNombreDepartamento("IT");
    }
}
