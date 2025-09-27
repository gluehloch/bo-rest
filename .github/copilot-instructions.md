# BetOffice REST API

BetOffice is a Java-based sports betting office application with REST API, web frontend, and Spring Boot deployment options. The project uses Maven for build management and MariaDB for data persistence.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Prerequisites and Setup
- Install Java 21 JDK (required for Spring Boot module):
  ```bash
  sudo apt update && sudo apt install -y openjdk-21-jdk
  export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
  java -version  # Should show Java 21
  ```
- **CRITICAL**: Always set JAVA_HOME before running Maven commands. Maven may default to Java 17 even when Java 21 is installed.
- Maven 3.x is required (usually pre-installed)
- MariaDB database server is required for full functionality

### Build Process
- **CRITICAL BUILD LIMITATION**: This project depends on external Maven repositories (maven.gluehloch.de) that may not be accessible in all environments
- **ALWAYS** set JAVA_HOME before building:
  ```bash
  export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
  mvn clean install  # NEVER CANCEL: Takes 10-15 minutes. Set timeout to 30+ minutes.
  ```
- If build fails due to repository access issues:
  - Document the specific error in your changes
  - The build requires custom parent POM (betoffice-maven-parent) from external repository
  - Dependencies include: betoffice-storage, betoffice-openligadb, betoffice-testutils
- Expected build error example: "Could not transfer artifact de.winkler.betoffice:betoffice-maven-parent:pom:1.14.0"

### Database Setup
- Install MariaDB server:
  ```bash
  sudo apt install -y mariadb-server
  sudo systemctl start mariadb
  ```
- Create database and user:
  ```sql
  CREATE DATABASE betoffice;
  CREATE USER 'betoffice'@'localhost' IDENTIFIED BY 'betoffice';
  GRANT ALL PRIVILEGES ON betoffice.* TO 'betoffice'@'localhost';
  FLUSH PRIVILEGES;
  ```

### Running the Application

#### Spring Boot Application (Recommended)
- Build the betoffice-springboot module first
- Run with:
  ```bash
  cd betoffice-springboot
  export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
  mvn spring-boot:run  # NEVER CANCEL: Startup takes 2-5 minutes. Set timeout to 10+ minutes.
  ```
- Application will be available at: `http://localhost:7878/bo`
- Context path: `/bo`
- Default port: 7878

#### WAR Deployment
- Deploy `betoffice-war/target/betoffice-war-1.5.1-SNAPSHOT.war` to application server
- Configure MariaDB datasource in application server

### Testing
- Unit tests are located in `src/test/java` directories
- **ALWAYS** set JAVA_HOME before running tests:
  ```bash
  export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
  mvn test  # NEVER CANCEL: Takes 5-10 minutes. Set timeout to 20+ minutes.
  ```
- Integration tests require database connectivity
- Test execution will fail if build dependencies are not available
- Key test files include (14 total test files):
  - `AuthenticationControllerTest.java`
  - `SeasonJsonMapperTest.java`
  - `TeamResultJsonMapperTest.java`
  - `TippControllerTest.java`

### Validation
- **Environment Validation Checklist**:
  1. `java -version` shows Java 21
  2. `export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64` is set
  3. `sudo systemctl is-active mariadb` returns "active"
  4. `mysql -u betoffice -pbetoffice -e "SELECT 'SUCCESS';" betoffice` succeeds
- **Manual Validation Scenarios** (once build succeeds):
  1. Start Spring Boot application and verify it starts without errors
  2. Access health endpoint: `GET http://localhost:7878/bo/actuator/health`
  3. Test authentication endpoint: `POST http://localhost:7878/bo/auth/login`
  4. Verify database connectivity through application logs
- **Build Validation**: 
  - If build fails with "Non-resolvable parent POM" error, this is expected due to external repository dependency
  - Document the specific Maven repository error in any changes you make

## Common Tasks

### Project Structure
```
betoffice-rest/          # REST API implementation (JAR) - 79 Java files
├── src/main/java/       # Java source code
├── src/main/resources/  # Configuration files (bodev.properties, version.properties)
├── src/test/           # Unit tests (13 test files)
└── pom.xml             # Maven configuration

betoffice-war/          # Web application (WAR) - 0 Java files (packaging only)
├── src/                # Web application source
└── pom.xml            

betoffice-springboot/   # Spring Boot application (JAR) - 1 Java file
├── src/main/java/     # Spring Boot main class (BetofficeBootApplication)
├── src/main/resources/ # Application properties
└── pom.xml            # Spring Boot Maven config

pom.xml                # Root Maven POM (multi-module)
```

**Project Statistics**:
- Total Java files: 93
- Total test files: 14
- Controller classes: 8 (AuthenticationController, TippController, SeasonController, etc.)
- Main modules: 3 (REST API, WAR packaging, Spring Boot app)

### Key Configuration Files
- `betoffice-springboot/src/main/resources/application.properties`:
  ```properties
  server.servlet.contextPath = /bo
  server.port = 7878
  ```
- `betoffice-rest/src/main/resources/bodev.properties`:
  ```properties
  betoffice.persistence.username = betoffice
  betoffice.persistence.password = betoffice
  betoffice.persistence.url = jdbc:mariadb://127.0.0.1/betoffice
  betoffice.persistence.classname = org.mariadb.jdbc.Driver
  betoffice.persistence.dialect = org.hibernate.dialect.MariaDBDialect
  ```

### Main Application Class
- Spring Boot entry point: `de.betoffice.web.boot.BetofficeBootApplication`
- Scans packages: `de.betoffice`, `de.winkler.betoffice`
- Auto-configuration excludes DataSource (configured manually)

### Key Dependencies
- Spring Boot 3.2.10
- Spring Security 6.1.5
- MariaDB Java Client 3.3.3
- Jackson 2.12.3+ for JSON processing
- Jakarta Servlet API 6.0.0

### Development Workflow
1. Always set Java 21 environment: `export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64`
2. Ensure MariaDB is running and database exists
3. Build with Maven: `mvn clean install` (if repositories accessible)
4. Run Spring Boot app: `cd betoffice-springboot && mvn spring-boot:run`
5. Test endpoints at `http://localhost:7878/bo`

### Troubleshooting
- **Build fails with repository errors**: External Maven repositories not accessible
- **Application won't start**: Check Java 21 is active and MariaDB is running
- **Database connection errors**: Verify MariaDB service and betoffice database exists
- **Port conflicts**: Change server.port in application.properties if 7878 is in use

### CI/CD Information
- Jenkins pipeline exists: `betoffice-rest/Jenkinsfile`
- Pipeline uses Maven 3.6.0 and JDK 11 (may need updating for Java 21)
- Deployment target: Custom Maven repository (maven.gluehloch.de)

**IMPORTANT**: This project requires external Maven repositories that may not be accessible in all environments. If build fails with repository errors, this is expected and should be documented in any changes you make.