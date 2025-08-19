package com.empleados.sistema.repository;

import com.empleados.sistema.model.Departamento;
import com.empleados.sistema.model.Empleado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class EmpleadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    private Empleado empleado;
    private Departamento departamento;

    @BeforeEach
    void setUp() {
        // Crear y persistir un departamento
        departamento = new Departamento();
        departamento.setNombre("IT");
        departamento.setDescripcion("Departamento de Tecnología");
        departamento = entityManager.persistAndFlush(departamento);

        // Crear y persistir un empleado
        empleado = new Empleado();
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setEmail("juan.perez@empresa.com");
        empleado.setFechaContratacion(LocalDate.now().minusYears(1));
        empleado.setSalario(new BigDecimal("50000.00"));
        empleado.setDepartamento(departamento);
        empleado = entityManager.persistAndFlush(empleado);

        // Crear y persistir otro empleado en el mismo departamento
        Empleado otroEmpleado = new Empleado();
        otroEmpleado.setNombre("Ana");
        otroEmpleado.setApellido("Gómez");
        otroEmpleado.setEmail("ana.gomez@empresa.com");
        otroEmpleado.setFechaContratacion(LocalDate.now().minusMonths(6));
        otroEmpleado.setSalario(new BigDecimal("45000.00"));
        otroEmpleado.setDepartamento(departamento);
        entityManager.persistAndFlush(otroEmpleado);
    }

    @Test
    void debeBuscarPorEmail() {
        // Act
        Optional<Empleado> encontrado = empleadoRepository.findByEmail("juan.perez@empresa.com");

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Juan", encontrado.get().getNombre());
        assertEquals("Pérez", encontrado.get().getApellido());
    }

    @Test
    void debeVerificarSiExisteEmail() {
        // Act
        boolean existe = empleadoRepository.existsByEmail("juan.perez@empresa.com");
        boolean noExiste = empleadoRepository.existsByEmail("noexiste@empresa.com");

        // Assert
        assertTrue(existe);
        assertFalse(noExiste);
    }

    @Test
    void debeBuscarPorDepartamento() {
        // Act
        List<Empleado> empleados = empleadoRepository.findByDepartamento(departamento);

        // Assert
        assertFalse(empleados.isEmpty());
        assertEquals(2, empleados.size());
        assertEquals("IT", empleados.get(0).getDepartamento().getNombre());
    }

    @Test
    void debeBuscarPorNombreDepartamento() {
        // Act
        List<Empleado> empleados = empleadoRepository.findByNombreDepartamento("IT");

        // Assert
        assertFalse(empleados.isEmpty());
        assertEquals(2, empleados.size());
        assertEquals("IT", empleados.get(0).getDepartamento().getNombre());
    }

    @Test
    void debeBuscarPorRangoSalario() {
        // Act
        List<Empleado> empleados = empleadoRepository.findBySalarioBetween(
                new BigDecimal("40000.00"), 
                new BigDecimal("60000.00"));

        // Assert
        assertFalse(empleados.isEmpty());
        assertEquals(2, empleados.size());
    }

    @Test
    void debeCalcularSalarioPromedioPorDepartamento() {
        // Act
        Optional<BigDecimal> promedio = empleadoRepository.findAverageSalarioByDepartamento(departamento.getId());

        // Assert
        assertTrue(promedio.isPresent());
        // El promedio debería ser (50000 + 45000) / 2 = 47500
        assertEquals(0, new BigDecimal("47500.00").compareTo(promedio.get()));
    }

    @Test
    void debeContarEmpleadosPorDepartamento() {
        // Act
        Long cantidad = empleadoRepository.countByDepartamentoId(departamento.getId());

        // Assert
        assertEquals(2L, cantidad);
    }
}
