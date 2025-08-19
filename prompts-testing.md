# Prompts de Testing

## Prompt 1: Configuración de pruebas unitarias con JUnit y Mockito

### Prompt Utilizado:
```
Necesito implementar pruebas unitarias para mi EmpleadoService usando JUnit 5 y Mockito. El servicio tiene los siguientes métodos:
- guardar(Empleado empleado)
- buscarPorId(Long id)
- obtenerTodos()
- actualizar(Long id, Empleado empleado)
- eliminar(Long id)
- buscarPorEmail(String email)
- buscarPorDepartamento(String nombreDepartamento)

Quiero probar casos exitosos y casos de error (como email duplicado o empleado no encontrado). ¿Cómo estructuro estas pruebas correctamente?
```

### Respuesta Recibida:
```java
@ExtendWith(MockitoExtension.class)
public class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Juan");
        empleado.setEmail("juan@empresa.com");
        // ... otros campos
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
        assertEquals("juan@empresa.com", resultado.getEmail());
        verify(empleadoRepository).existsByEmail("juan@empresa.com");
        verify(empleadoRepository).save(empleado);
    }

    @Test
    void debeLanzarExcepcionCuandoEmailYaExiste() {
        // Arrange
        when(empleadoRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(EmailDuplicadoException.class, () -> empleadoService.guardar(empleado));
        verify(empleadoRepository).existsByEmail("juan@empresa.com");
        verify(empleadoRepository, never()).save(any(Empleado.class));
    }
}
```

### Modificaciones Realizadas:
- Agregué más casos de prueba para cubrir todos los métodos del servicio
- Incluí pruebas para validaciones de negocio (salario positivo, campos obligatorios)
- Mejoré los asserts para verificar comportamiento específico
- Agregué pruebas para operaciones de asignación (departamento, proyecto)
- Estructuré las pruebas con el patrón AAA (Arrange, Act, Assert)

### Explicación del Prompt:
Necesitaba crear pruebas unitarias robustas para la capa de servicio. Las pruebas unitarias deben verificar la lógica de negocio aislando la capa de servicio mediante mocks de los repositorios.

### Aprendizajes Obtenidos:
- @ExtendWith(MockitoExtension.class) configura JUnit 5 con Mockito
- @Mock crea objetos simulados de dependencias
- @InjectMocks inyecta los mocks en la clase a probar
- when().thenReturn() configura el comportamiento de los mocks
- verify() confirma que los métodos del mock fueron llamados
- assertThrows() verifica que se lancen excepciones específicas
- never() verifica que un método nunca fue llamado
- El patrón AAA (Arrange, Act, Assert) estructura claramente las pruebas

---

## Prompt 2: Pruebas de integración para controladores REST

### Prompt Utilizado:
```
Necesito implementar pruebas de integración para mi EmpleadoController usando @WebMvcTest. El controlador tiene endpoints para:
- GET /api/empleados
- GET /api/empleados/{id}
- POST /api/empleados
- PUT /api/empleados/{id}
- DELETE /api/empleados/{id}

¿Cómo configuro estas pruebas para verificar status codes, respuestas JSON y manejo de errores?
```

### Respuesta Recibida:
```java
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
        // ... otros campos
    }

    @Test
    void debeObtenerTodosLosEmpleados() throws Exception {
        List<Empleado> empleados = Arrays.asList(empleado);
        when(empleadoService.obtenerTodos()).thenReturn(empleados);

        mockMvc.perform(get("/api/empleados"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nombre", is("Juan")));
    }

    @Test
    void debeCrearEmpleado() throws Exception {
        when(empleadoService.guardar(any(Empleado.class))).thenReturn(empleado);

        mockMvc.perform(post("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleado)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)));
    }
}
```

### Modificaciones Realizadas:
- Agregué pruebas para todos los endpoints del controlador
- Incluí pruebas para casos de error (404, 400, 409)
- Mejoré las verificaciones de respuestas JSON
- Agregué pruebas para validación de datos con @Valid
- Implementé pruebas para endpoints de búsqueda específicos

### Explicación del Prompt:
Las pruebas de integración para controladores verifican la capa web completa, incluyendo serialización/deserialización JSON, mapeo de rutas, códigos de estado HTTP y manejo de excepciones.

### Aprendizajes Obtenidos:
- @WebMvcTest configura el contexto Spring para pruebas de controladores
- MockMvc simula peticiones HTTP sin iniciar un servidor real
- @MockBean reemplaza beans del contexto con mocks
- jsonPath permite verificar la estructura de respuestas JSON
- ObjectMapper serializa/deserializa objetos Java a/desde JSON
- andExpect verifica diferentes aspectos de la respuesta HTTP
- MediaType.APPLICATION_JSON especifica el formato de contenido
- status().isOk() verifica código 200, isCreated() verifica 201, etc.

---

## Prompt 3: Pruebas de repositorio con @DataJpaTest

### Prompt Utilizado:
```
Quiero implementar pruebas para mi EmpleadoRepository usando @DataJpaTest. Necesito probar:
- findByEmail
- existsByEmail
- findByDepartamento
- findByNombreDepartamento
- findBySalarioBetween
- findAverageSalarioByDepartamento

¿Cómo configuro correctamente estas pruebas con H2 en memoria?
```

### Respuesta Recibida:
```java
@DataJpaTest
@ActiveProfiles("test")
public class EmpleadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    private Empleado empleado;
    private Departamento departamento;

    @BeforeEach
    void setUp() {
        departamento = new Departamento();
        departamento.setNombre("IT");
        departamento = entityManager.persistAndFlush(departamento);

        empleado = new Empleado();
        empleado.setNombre("Juan");
        empleado.setEmail("juan@empresa.com");
        empleado.setDepartamento(departamento);
        empleado = entityManager.persistAndFlush(empleado);
    }

    @Test
    void debeBuscarPorEmail() {
        Optional<Empleado> encontrado = empleadoRepository.findByEmail("juan@empresa.com");
        assertTrue(encontrado.isPresent());
        assertEquals("Juan", encontrado.get().getNombre());
    }

    @Test
    void debeBuscarPorDepartamento() {
        List<Empleado> empleados = empleadoRepository.findByDepartamento(departamento);
        assertFalse(empleados.isEmpty());
        assertEquals("IT", empleados.get(0).getDepartamento().getNombre());
    }
}
```

### Modificaciones Realizadas:
- Agregué configuración específica para H2 en application-test.yml
- Incluí pruebas para todas las consultas personalizadas
- Mejoré los datos de prueba para cubrir más casos
- Agregué verificaciones más detalladas en los asserts
- Implementé pruebas para métodos de agregación (promedio, conteo)

### Explicación del Prompt:
Las pruebas de repositorio verifican que las consultas JPA y JPQL funcionen correctamente contra una base de datos real (aunque en memoria). Esto es crucial para validar el mapeo objeto-relacional.

### Aprendizajes Obtenidos:
- @DataJpaTest configura un contexto Spring para pruebas de JPA
- TestEntityManager es una versión simplificada de EntityManager para pruebas
- @ActiveProfiles("test") activa la configuración específica para pruebas
- persistAndFlush guarda entidades y sincroniza con la base de datos
- Las pruebas de repositorio son más lentas que las unitarias pero más realistas
- H2 en memoria es ideal para pruebas por su velocidad y aislamiento
- Las relaciones entre entidades deben configurarse correctamente en las pruebas
- Las consultas personalizadas con @Query deben probarse explícitamente

---

## Prompt 4: Configuración de application-test.yml para pruebas

### Prompt Utilizado:
```
Necesito configurar application-test.yml para mis pruebas de integración y repositorio. Quiero usar H2 en memoria con estas características:
- Base de datos que no se cierre entre pruebas
- Mostrar SQL para debugging
- Crear tablas automáticamente
- Configuración de logging para ver errores SQL
- Dialect específico para H2

¿Cuál sería la configuración óptima?
```

### Respuesta Recibida:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.H2Dialect
  h2:
    console:
      enabled: true

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Modificaciones Realizadas:
- Agregué configuración específica para com.empleados.sistema en DEBUG
- Ajusté el nivel de logging para reducir ruido en la consola
- Mantuve la configuración de H2 optimizada para pruebas
- Aseguré que DB_CLOSE_DELAY=-1 para mantener la base de datos viva entre pruebas

### Explicación del Prompt:
La configuración específica para pruebas debe optimizar velocidad y visibilidad de errores, mientras mantiene un entorno aislado y reproducible.

### Aprendizajes Obtenidos:
- DB_CLOSE_DELAY=-1 evita que H2 cierre la conexión entre pruebas
- show-sql y format_sql facilitan el debugging de consultas SQL
- create-drop recrea el esquema para cada ejecución de pruebas
- Los niveles de logging DEBUG/TRACE muestran información detallada
- org.hibernate.type.descriptor.sql.BasicBinder muestra valores de parámetros
- La configuración específica para pruebas debe estar en src/test/resources
- @ActiveProfiles("test") activa esta configuración durante las pruebas

---
