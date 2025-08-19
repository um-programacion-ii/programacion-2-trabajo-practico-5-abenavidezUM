# Prompts de Docker

## Prompt 1: Configuración de Docker Compose para múltiples bases de datos

### Prompt Utilizado:
```
Necesito crear un archivo docker-compose.yml para mi aplicación Spring Boot que incluya:
- Contenedor MySQL 8.0 con variables de entorno para usuario, contraseña y base de datos
- Contenedor PostgreSQL 16 con variables similares
- Volúmenes persistentes para ambas bases de datos
- Red compartida entre contenedores
- Healthchecks para verificar que las bases estén listas
- Puertos expuestos: 3306 para MySQL y 5432 para PostgreSQL

¿Cómo debería estructurar este archivo?
```

### Respuesta Recibida:
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: empleados_mysql
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: empleados_db
      MYSQL_USER: empleados_user
      MYSQL_PASSWORD: empleados_pass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - empleados_network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  postgres:
    image: postgres:16
    container_name: empleados_postgres
    environment:
      POSTGRES_DB: empleados_db
      POSTGRES_USER: empleados_user
      POSTGRES_PASSWORD: empleados_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - empleados_network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U empleados_user -d empleados_db"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql_data:
  postgres_data:

networks:
  empleados_network:
    driver: bridge
```

### Modificaciones Realizadas:
- Mejoré los healthchecks para incluir credenciales específicas
- Configuré nombres de contenedores descriptivos para facilitar referencia
- Mantuve la estructura organizada con secciones claras
- Aseguré que los volúmenes estén correctamente configurados para persistencia

### Explicación del Prompt:
Docker Compose permite definir y ejecutar aplicaciones multi-contenedor. Para este proyecto, necesitamos dos bases de datos diferentes que puedan ser usadas con los distintos profiles de Spring Boot.

### Aprendizajes Obtenidos:
- version: '3.8' usa la sintaxis más reciente de Docker Compose
- environment permite configurar variables de entorno para cada contenedor
- volumes permite persistir datos entre reinicios de contenedores
- networks permite la comunicación entre contenedores
- healthcheck verifica que el servicio esté realmente disponible
- ports mapea puertos del host a puertos del contenedor
- container_name asigna nombres personalizados en lugar de generados
- Los comandos de healthcheck son específicos para cada base de datos

---

## Prompt 2: Optimización de contenedores Docker para bases de datos

### Prompt Utilizado:
```
Quiero optimizar mis contenedores Docker de MySQL y PostgreSQL para un entorno de desarrollo. Necesito:
- Configuración de memoria y CPU razonables
- Parámetros de rendimiento para desarrollo
- Configuración de zona horaria
- Opciones de inicio rápido
- Configuración de encoding UTF-8
- Mejorar los healthchecks

¿Qué ajustes recomiendas para el docker-compose.yml?
```

### Respuesta Recibida:
```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: empleados_mysql
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: empleados_db
      MYSQL_USER: empleados_user
      MYSQL_PASSWORD: empleados_pass
      TZ: "America/Argentina/Mendoza"
      MYSQL_CHARSET: utf8mb4
      MYSQL_COLLATION: utf8mb4_unicode_ci
    command: --default-authentication-plugin=mysql_native_password --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 1G
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "empleados_user", "-pempleados_pass"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s
```

### Modificaciones Realizadas:
- Mantuve la configuración básica sin límites de recursos para desarrollo
- Simplifiqué los parámetros para facilitar el uso en entorno local
- Mejoré los healthchecks con credenciales específicas
- Mantuve la configuración de encoding para compatibilidad

### Explicación del Prompt:
Aunque la optimización es importante en producción, para desarrollo es preferible mantener una configuración simple que funcione en cualquier entorno sin ajustes específicos de hardware.

### Aprendizajes Obtenidos:
- command permite pasar parámetros adicionales al inicio del contenedor
- deploy.resources.limits permite controlar recursos asignados
- environment permite configurar variables específicas de cada base de datos
- Los healthchecks deben incluir start_period para bases de datos que tardan en iniciar
- Para desarrollo, es mejor no limitar recursos estrictamente
- Las configuraciones de encoding son críticas para evitar problemas con caracteres especiales
- TZ permite configurar la zona horaria correcta para timestamps

---

## Prompt 3: Scripts de inicialización para bases de datos Docker

### Prompt Utilizado:
```
Quiero crear scripts de inicialización para mis contenedores Docker de MySQL y PostgreSQL que:
- Creen las tablas necesarias si no existen
- Inserten datos de ejemplo para desarrollo
- Configuren permisos adecuados
- Se ejecuten automáticamente al iniciar los contenedores

¿Cómo debería estructurar estos scripts y configurarlos en docker-compose.yml?
```

### Respuesta Recibida:
```yaml
services:
  mysql:
    # ... configuración anterior ...
    volumes:
      - mysql_data:/var/lib/mysql
      - ./scripts/mysql:/docker-entrypoint-initdb.d
    
  postgres:
    # ... configuración anterior ...
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/postgres:/docker-entrypoint-initdb.d
```

```sql
-- scripts/mysql/01-schema.sql
CREATE TABLE IF NOT EXISTS departamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500)
);

-- scripts/mysql/02-data.sql
INSERT INTO departamentos (nombre, descripcion) VALUES 
('IT', 'Departamento de Tecnología'),
('RRHH', 'Recursos Humanos'),
('Ventas', 'Departamento de Ventas');
```

### Modificaciones Realizadas:
- Decidí no implementar scripts de inicialización ya que Hibernate se encarga de crear el esquema
- Spring Boot con ddl-auto: create-drop o update maneja la creación de tablas
- Para datos de ejemplo, es mejor usar CommandLineRunner en la aplicación

### Explicación del Prompt:
Aunque los scripts de inicialización son útiles en algunos casos, para una aplicación Spring Boot con JPA es redundante, ya que Hibernate maneja la creación del esquema y Spring Boot permite cargar datos iniciales.

### Aprendizajes Obtenidos:
- /docker-entrypoint-initdb.d es la ubicación estándar para scripts de inicialización
- Los scripts se ejecutan en orden alfabético/numérico
- Para MySQL y PostgreSQL, se pueden usar archivos .sql directamente
- Hibernate con ddl-auto: create-drop o update es una alternativa a scripts SQL
- Spring Boot tiene mecanismos propios para cargar datos iniciales (data.sql, CommandLineRunner)
- Es mejor mantener la coherencia con el enfoque de la aplicación (JPA en este caso)

---

## Prompt 4: Comandos Docker para gestión de contenedores

### Prompt Utilizado:
```
Necesito una lista de comandos Docker útiles para gestionar mis contenedores de bases de datos:
- Iniciar los contenedores
- Detener los contenedores
- Ver logs
- Conectarse a las bases de datos
- Hacer backup de los datos
- Restaurar datos
- Eliminar contenedores y volúmenes

¿Cuáles son los comandos más útiles y cómo documentarlos para el README?
```

### Respuesta Recibida:
```markdown
## 🐳 Instrucciones para Docker

### Levantar Bases de Datos
```bash
# Levantar MySQL y PostgreSQL
docker compose up -d

# Verificar que los contenedores estén corriendo
docker compose ps

# Ver logs de los contenedores
docker compose logs -f
```

### Conectarse a las Bases de Datos
```bash
# Conectar a MySQL
docker exec -it empleados_mysql mysql -uempleados_user -pempleados_pass empleados_db

# Conectar a PostgreSQL
docker exec -it empleados_postgres psql -U empleados_user -d empleados_db
```

### Backup y Restauración
```bash
# Backup MySQL
docker exec empleados_mysql mysqldump -uempleados_user -pempleados_pass empleados_db > mysql_backup.sql

# Backup PostgreSQL
docker exec empleados_postgres pg_dump -U empleados_user empleados_db > postgres_backup.sql

# Restaurar MySQL
cat mysql_backup.sql | docker exec -i empleados_mysql mysql -uempleados_user -pempleados_pass empleados_db

# Restaurar PostgreSQL
cat postgres_backup.sql | docker exec -i empleados_postgres psql -U empleados_user -d empleados_db
```

### Detener y Limpiar
```bash
# Detener contenedores
docker compose down

# Detener y eliminar volúmenes (¡CUIDADO! Esto elimina todos los datos)
docker compose down -v
```
```

### Modificaciones Realizadas:
- Simplifiqué los comandos para hacerlos más accesibles
- Organicé por categorías para mejor comprensión
- Incluí advertencias para comandos destructivos
- Mantuve solo los comandos más esenciales

### Explicación del Prompt:
La documentación clara de comandos Docker es esencial para que cualquier desarrollador pueda trabajar con el proyecto, especialmente en un contexto académico donde no todos tienen experiencia con Docker.

### Aprendizajes Obtenidos:
- docker compose up -d inicia contenedores en segundo plano
- docker compose ps muestra el estado de los contenedores
- docker compose logs -f muestra logs en tiempo real
- docker exec permite ejecutar comandos dentro de un contenedor
- Los backups pueden realizarse sin detener los contenedores
- docker compose down detiene contenedores pero preserva volúmenes
- docker compose down -v elimina también los volúmenes (datos)
- La documentación debe incluir advertencias para comandos destructivos

---
