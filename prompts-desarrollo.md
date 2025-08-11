# Prompts de Desarrollo - Etapa 1

## Prompt 1: Configuración inicial del proyecto Spring Boot

### Prompt Utilizado:
```
Necesito ayuda para configurar un proyecto Spring Boot con JPA para un sistema de gestión de empleados. El proyecto debe usar:
- Spring Boot 3.5.0
- Java 17
- Spring Data JPA 
- H2 para desarrollo
- MySQL y PostgreSQL para producción
- Maven como build tool

¿Puedes ayudarme a crear el pom.xml con todas las dependencias necesarias?
```

### Respuesta Recibida:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.0</version>
		<relativePath/>
	</parent>
	<groupId>com.empleados</groupId>
	<artifactId>sistema-gestion-empleados</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>sistema-gestion-empleados</name>
	<description>Sistema de Gestión de Empleados con JPA y Spring Boot</description>
	
	<properties>
		<java.version>17</java.version>
	</properties>
	
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
</project>
```

### Modificaciones Realizadas:
- Cambié inicialmente Java 21 por Java 17 para compatibilidad
- Ajusté el groupId y artifactId para ser más específicos del proyecto
- Mantuve las dependencias esenciales sin cambios

### Explicación del Prompt:
Usé este prompt porque necesitaba una base sólida para el proyecto. Especifiqué claramente las tecnologías requeridas según la consigna del trabajo práctico, incluyendo las versiones específicas mencionadas en el README.

### Aprendizajes Obtenidos:
- La importancia de especificar versiones exactas en proyectos académicos
- Cómo Spring Boot parent POM simplifica la gestión de dependencias
- La diferencia entre dependencias de runtime y compile time
- Que H2 es ideal para desarrollo por ser en memoria
- La estructura básica de un proyecto Maven con Spring Boot

---

## Prompt 2: Diseño de entidades JPA

### Prompt Utilizado:
```
Tengo que crear tres entidades JPA para un sistema de empleados:

1. Empleado: id, nombre, apellido, email (único), fechaContratacion, salario, departamento
2. Departamento: id, nombre (único), descripcion, empleados
3. Proyecto: id, nombre, descripcion, fechaInicio, fechaFin, empleados

Las relaciones son:
- Empleado pertenece a un Departamento (ManyToOne)
- Departamento tiene muchos Empleados (OneToMany)
- Empleado puede trabajar en muchos Proyectos (ManyToMany)
- Proyecto puede tener muchos Empleados (ManyToMany)

¿Puedes ayudarme a implementar estas entidades con las anotaciones JPA correctas? Quiero usar getters/setters manuales, no Lombok.
```

### Respuesta Recibida:
```java
// Entidad Departamento
@Entity
@Table(name = "departamentos")
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100, unique = true)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Empleado> empleados = new ArrayList<>();
    
    // constructores, getters, setters...
}

// Entidad Empleado  
@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;
    
    @ManyToMany
    @JoinTable(
        name = "empleado_proyecto",
        joinColumns = @JoinColumn(name = "empleado_id"),
        inverseJoinColumns = @JoinColumn(name = "proyecto_id")
    )
    private Set<Proyecto> proyectos = new HashSet<>();
    
    // constructores, getters, setters...
}

// Entidad Proyecto
@Entity
@Table(name = "proyectos")
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
    
    @ManyToMany(mappedBy = "proyectos")
    private Set<Empleado> empleados = new HashSet<>();
    
    // constructores, getters, setters...
}
```

### Modificaciones Realizadas:
- Agregué constructores sin argumentos y con parámetros
- Implementé todos los getters y setters manualmente
- Agregué métodos de utilidad como `getNombreCompleto()` en Empleado
- Incluí métodos helper para gestionar relaciones bidireccionales
- Agregué método `estaActivo()` en Proyecto para verificar estado

### Explicación del Prompt:
Necesitaba implementar las entidades de manera que cumplieran con los requisitos del diagrama de clases del trabajo práctico. Especifiqué que no quería usar Lombok para practicar la escritura manual de getters/setters, lo cual es más educativo.

### Aprendizajes Obtenidos:
- Importancia de `mappedBy` en relaciones bidireccionales para evitar tablas innecesarias
- Diferencia entre `Set` y `List` para relaciones (Set evita duplicados en ManyToMany)
- Uso de `@JoinTable` para personalizar nombres de tabla intermedia
- `FetchType.LAZY` mejora performance al cargar relaciones bajo demanda
- `CascadeType.ALL` propaga operaciones a entidades relacionadas
- Importancia de inicializar colecciones para evitar NullPointerException

---

## Prompt 3: Configuración de profiles para múltiples bases de datos

### Prompt Utilizado:
```
Necesito configurar application.yml para trabajar con tres profiles diferentes:
1. dev: usar H2 en memoria con consola habilitada
2. mysql: conectar a MySQL en localhost:3306
3. postgres: conectar a PostgreSQL en localhost:5432

También quiero que muestre las queries SQL formateadas para debugging. ¿Puedes ayudarme con la configuración?
```

### Respuesta Recibida:
```yaml
spring:
  profiles:
    active: dev

---
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

---
spring:
  config:
    activate:
      on-profile: mysql
  datasource:
    url: jdbc:mysql://localhost:3306/empleados_db?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: empleados_user
    password: empleados_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true

---
spring:
  config:
    activate:
      on-profile: postgres
  datasource:
    url: jdbc:postgresql://localhost:5432/empleados_db
    driver-class-name: org.postgresql.Driver
    username: empleados_user
    password: empleados_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```

### Modificaciones Realizadas:
- Configuré el profile dev como activo por defecto
- Ajusté parámetros de conexión específicos para cada base de datos
- Mantuve configuración de logging SQL en todos los profiles para debugging

### Explicación del Prompt:
Este prompt fue necesario para implementar uno de los requisitos clave del trabajo: poder ejecutar la aplicación con diferentes bases de datos usando profiles. Esto simula un entorno real donde desarrollo, testing y producción usan diferentes DBs.

### Aprendizajes Obtenidos:
- Los profiles de Spring permiten configuraciones condicionales
- `ddl-auto: create-drop` es ideal para desarrollo (recrea schema cada vez)
- `ddl-auto: update` es mejor para persistencia de datos en testing/prod
- Cada base de datos tiene su propio dialecto de Hibernate
- `show-sql: true` y `format_sql: true` son invaluables para debugging
- La sintaxis `---` separa diferentes configuraciones de profile en YAML
- Parámetros como `useSSL=false` y `serverTimezone=UTC` son necesarios para MySQL

---

## Prompt 4: Verificación de compilación y testing básico

### Prompt Utilizado:
```
He creado mi proyecto Spring Boot con las entidades JPA. ¿Qué comandos debo ejecutar para verificar que todo compile correctamente? También quiero probar que la aplicación inicie correctamente con H2.
```

### Respuesta Recibida:
```bash
# Compilar el proyecto
./mvnw clean compile

# Empaquetar (incluye tests)
./mvnw clean package

# Ejecutar la aplicación con profile dev (H2)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# O alternativamente:
java -jar target/sistema-gestion-empleados-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Para acceder a la consola H2:
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (vacío)
```

### Modificaciones Realizadas:
- Tuve que crear manualmente el wrapper de Maven porque no estaba incluido
- Cambié la versión de Java de 21 a 17 para compatibilidad con el sistema actual
- Verifiqué que todas las dependencias se descargaran correctamente

### Explicación del Prompt:
Necesitaba validar que mi implementación funcionara correctamente antes de continuar. Es una buena práctica compilar y probar temprano para detectar errores antes de que se acumulen.

### Aprendizajes Obtenidos:
- `./mvnw clean compile` verifica sintaxis y dependencias sin ejecutar tests
- Maven wrapper (`mvnw`) hace el proyecto más portable
- Spring Boot hace muy fácil cambiar entre profiles con parámetros de línea de comandos
- H2 console es una herramienta visual muy útil para verificar esquemas generados
- Los mensajes de compilación de Maven son muy informativos para debugging
- La diferencia entre `compile` y `package` - compile solo compila, package incluye tests y empaquetado

---

# Prompts de Desarrollo - Etapa 2

## Prompt 5: Diseño de interfaces de repositorio JPA

### Prompt Utilizado:
```
Necesito crear interfaces de repositorio para mis entidades JPA (Empleado, Departamento, Proyecto). Quiero que hereden de JpaRepository y que incluyan:

1. EmpleadoRepository: métodos para buscar por email, departamento, rango de salario, fecha de contratación
2. DepartamentoRepository: métodos para buscar por nombre, calcular salario promedio
3. ProyectoRepository: métodos para proyectos activos/inactivos, buscar por fechas, contar empleados

También necesito consultas personalizadas con @Query para casos más complejos. ¿Puedes ayudarme?
```

### Respuesta Recibida:
```java
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Empleado> findByDepartamento(Departamento departamento);
    List<Empleado> findBySalarioBetween(BigDecimal salarioMin, BigDecimal salarioMax);
    
    @Query("SELECT e FROM Empleado e WHERE e.departamento.nombre = :nombreDepartamento")
    List<Empleado> findByNombreDepartamento(@Param("nombreDepartamento") String nombreDepartamento);
    
    @Query("SELECT AVG(e.salario) FROM Empleado e WHERE e.departamento.id = :departamentoId")
    Optional<BigDecimal> findAverageSalarioByDepartamento(@Param("departamentoId") Long departamentoId);
}

// Repositorios similares para Departamento y Proyecto...
```

### Modificaciones Realizadas:
- Agregué más consultas personalizadas como buscar por año de contratación
- Incluí métodos para buscar por nombre o apellido con LIKE case-insensitive
- Agregué validaciones de existencia con métodos `exists`
- Implementé consultas para manejar relaciones Many-to-Many con proyectos

### Explicación del Prompt:
Necesitaba implementar la capa de persistencia con Spring Data JPA. Los métodos derivados facilitan consultas simples, mientras que @Query permite consultas más complejas con JPQL.

### Aprendizajes Obtenidos:
- Spring Data JPA genera automáticamente implementaciones de métodos derivados
- @Query permite escribir JPQL personalizado para consultas complejas
- @Param vincula parámetros de método con parámetros de query
- Optional<T> es mejor práctica para valores que pueden no existir
- JPQL es más orientado a objetos que SQL nativo
- Importancia de manejar relaciones en consultas (JOIN)

---

## Prompt 6: Implementación de servicios con transacciones

### Prompt Utilizado:
```
Tengo que implementar la capa de servicios para mi sistema de empleados. Necesito:

1. Interfaces de servicio con operaciones CRUD y de negocio
2. Implementaciones con @Service y @Transactional
3. Manejo de excepciones personalizadas
4. Validaciones de negocio (email único, relaciones válidas)
5. Métodos para asignar empleados a departamentos y proyectos

¿Puedes ayudarme con la estructura y las mejores prácticas?
```

### Respuesta Recibida:
```java
public interface EmpleadoService {
    Empleado guardar(Empleado empleado);
    Empleado buscarPorId(Long id);
    List<Empleado> obtenerTodos();
    void eliminar(Long id);
    
    // Métodos de negocio
    void asignarADepartamento(Long empleadoId, Long departamentoId);
    BigDecimal obtenerSalarioPromedioPorDepartamento(Long departamentoId);
}

@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {
    
    private final EmpleadoRepository empleadoRepository;
    
    @Override
    public Empleado guardar(Empleado empleado) {
        if (empleadoRepository.existsByEmail(empleado.getEmail())) {
            throw new EmailDuplicadoException("Email ya existe");
        }
        return empleadoRepository.save(empleado);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Empleado buscarPorId(Long id) {
        return empleadoRepository.findById(id)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("ID no encontrado"));
    }
}
```

### Modificaciones Realizadas:
- Implementé validaciones más robustas (campos obligatorios, salario positivo)
- Agregué más métodos de negocio como asignación a múltiples proyectos
- Incluí manejo de transacciones readOnly para consultas
- Mejoré mensajes de error para ser más descriptivos
- Agregué validaciones de fechas en proyectos

### Explicación del Prompt:
La capa de servicios es donde va la lógica de negocio. Necesitaba separar responsabilidades: repositories para persistencia, services para reglas de negocio y transacciones.

### Aprendizajes Obtenidos:
- @Transactional asegura consistencia de datos y rollback automático
- @Transactional(readOnly = true) optimiza consultas de solo lectura
- Inyección de dependencias por constructor es más robusta que @Autowired
- Excepciones unchecked (RuntimeException) causan rollback automático
- Validaciones deben estar en la capa de servicio, no en controladores
- Importancia de validar datos antes de persistir

---

## Prompt 7: Manejo de excepciones personalizadas

### Prompt Utilizado:
```
Necesito crear excepciones personalizadas para mi aplicación de empleados:
- EmpleadoNoEncontradoException
- EmailDuplicadoException  
- DepartamentoNoEncontradoException
- ProyectoNoEncontradoException

¿Cómo debo estructurarlas? ¿Deben heredar de Exception o RuntimeException? ¿Qué constructores incluir?
```

### Respuesta Recibida:
```java
public class EmpleadoNoEncontradoException extends RuntimeException {
    public EmpleadoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    public EmpleadoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String mensaje) {
        super(mensaje);
    }
    
    public EmailDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
```

### Modificaciones Realizadas:
- Mantuve las excepciones simples sin complejidad innecesaria
- Agregué constructores estándar que son útiles para debugging
- Cree una excepción para cada entidad principal del dominio

### Explicación del Prompt:
Las excepciones personalizadas mejoran la legibilidad del código y permiten manejo específico de errores. Heredar de RuntimeException evita el manejo obligatorio (checked exceptions).

### Aprendizajes Obtenidos:
- RuntimeException es mejor para errores de lógica de negocio
- Constructores estándar: solo mensaje y mensaje + causa
- Nombres descriptivos ayudan a identificar el problema rápidamente
- No sobrecomplicar con campos adicionales a menos que sea necesario
- Las excepciones de dominio facilitan el debugging y logging

---

## Prompt 8: Validaciones y lógica de negocio avanzada

### Prompt Utilizado:
```
En mis servicios necesito implementar validaciones más sofisticadas:

1. Validar que no se pueda eliminar un departamento con empleados
2. Verificar fechas válidas en proyectos (inicio < fin)
3. Asegurar que salarios sean positivos
4. Validar campos obligatorios antes de guardar
5. Controlar la asignación de empleados a proyectos (evitar duplicados)

¿Cómo estructuro estas validaciones en mis services?
```

### Respuesta Recibida:
```java
@Override
public Empleado guardar(Empleado empleado) {
    // Validaciones de datos
    if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
        throw new IllegalArgumentException("El nombre es obligatorio");
    }
    
    if (empleado.getSalario() == null || empleado.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("El salario debe ser mayor a cero");
    }
    
    // Validaciones de negocio
    if (empleadoRepository.existsByEmail(empleado.getEmail())) {
        throw new EmailDuplicadoException("Email ya existe: " + empleado.getEmail());
    }
    
    return empleadoRepository.save(empleado);
}

@Override
public void eliminar(Long id) {
    Departamento departamento = buscarPorId(id);
    
    if (!puedeEliminar(id)) {
        throw new IllegalStateException("No se puede eliminar - tiene empleados asignados");
    }
    
    departamentoRepository.delete(departamento);
}
```

### Modificaciones Realizadas:
- Implementé validaciones más exhaustivas para cada entidad
- Agregué métodos helper como `puedeEliminar()` para encapsular lógica
- Mejoré mensajes de error para incluir valores específicos
- Agregué validación de rangos en búsquedas por salario y fechas

### Explicación del Prompt:
Las validaciones robustas son críticas para mantener integridad de datos. Necesitaba balancear validaciones técnicas (campos nulos) con reglas de negocio (unicidad, relaciones).

### Aprendizajes Obtenidos:
- Separar validaciones técnicas de reglas de negocio
- IllegalArgumentException para datos inválidos
- IllegalStateException para estados de negocio inválidos
- Validar temprano y fallar rápido (fail-fast)
- Encapsular lógica compleja en métodos privados helper
- Mensajes de error específicos facilitan debugging para desarrolladores

---
