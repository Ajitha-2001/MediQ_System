# MediQ System

<div align="center">

![MediQ System](https://img.shields.io/badge/MediQ-Hospital%20Management-blue?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### 🏥 Modern Web-Based Hospital Management System

*Streamlining healthcare operations through digital transformation*

[Features](#-features) • [Installation](#%EF%B8%8F-installation--setup) • [Documentation](#-project-structure) • [Contributing](#-contributing)

</div>

---

## 📋 Overview

MediQ System is a comprehensive web-based hospital management solution designed to digitalize and optimize hospital operations. Built with modern technologies and best practices, it provides an intuitive interface for managing patients, doctors, appointments, and medical records while ensuring security and scalability.

**Project Details:**
- **Academic Project:** 2025-Y2-S1-MLB-B3G1-41
- **Type:** Web-Based Hospital Management System
- **Status:** Active Development

---

## ✨ Features

### Core Functionality
- 👥 **Patient Management** - Complete patient registration, profile management, and medical history tracking
- 👨‍⚕️ **Doctor & Staff Management** - Comprehensive staff profiles, specializations, and availability management
- 📅 **Appointment Scheduling** - Smart scheduling system with conflict detection and notifications
- 📋 **Medical Records** - Secure storage and retrieval of patient medical records and prescriptions
- 🔐 **Role-Based Access Control** - Granular permissions for Admin, Doctor, and Staff roles
- 📊 **Dashboard & Analytics** - Real-time insights and reporting capabilities
- 🔔 **Notifications** - Automated reminders for appointments and follow-ups
- 🔍 **Search & Filter** - Advanced search functionality across all modules

### Security Features
- Secure authentication and authorization
- Data encryption at rest and in transit
- Audit logging for compliance
- Session management and timeout protection

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17+, Spring Boot, Spring Security, Spring Data JPA |
| **Frontend** | HTML5, CSS3, JavaScript (ES6+), Bootstrap 5 |
| **Database** | MySQL 8.0+ |
| **Build Tool** | Maven |
| **API** | RESTful APIs |
| **Version Control** | Git |

---

## 📁 Project Structure

```
MediQ_System-main/
│
├── web_base_hospital_management_system/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/mediq/
│   │   │   │       ├── controller/      # REST Controllers
│   │   │   │       ├── service/         # Business Logic
│   │   │   │       ├── repository/      # Data Access Layer
│   │   │   │       ├── model/           # Entity Classes
│   │   │   │       ├── dto/             # Data Transfer Objects
│   │   │   │       ├── config/          # Configuration Classes
│   │   │   │       └── security/        # Security Components
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── static/              # CSS, JS, Images
│   │   │       ├── templates/           # HTML Templates
│   │   │       └── application.properties
│   │   │
│   │   └── test/                        # Unit & Integration Tests
│   │
│   ├── pom.xml                          # Maven Dependencies
│   ├── mvnw                             # Maven Wrapper (Unix)
│   └── mvnw.cmd                         # Maven Wrapper (Windows)
│
├── docs/                                # Documentation
├── .gitignore
└── README.md
```

---

## ⚙️ Installation & Setup

### Prerequisites

Ensure you have the following installed:

- ☕ **Java JDK 17** or higher ([Download](https://www.oracle.com/java/technologies/downloads/))
- 📦 **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- 🗄️ **MySQL 8.0+** ([Download](https://dev.mysql.com/downloads/))
- 🔧 **Git** ([Download](https://git-scm.com/downloads))
- 💻 **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Installation Steps

1️⃣ **Clone the repository**

```bash
git clone https://github.com/your-username/MediQ_System.git
cd MediQ_System/web_base_hospital_management_system
```

2️⃣ **Configure the database**

Create a MySQL database:

```sql
CREATE DATABASE mediq_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'mediq_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON mediq_db.* TO 'mediq_user'@'localhost';
FLUSH PRIVILEGES;
```

3️⃣ **Update application properties**

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mediq_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=mediq_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server Configuration
server.port=8080
server.servlet.context-path=/mediq

# Logging
logging.level.com.mediq=DEBUG
```

4️⃣ **Build the project**

```bash
mvn clean install
```

5️⃣ **Run the application**

Using Maven:
```bash
mvn spring-boot:run
```

Or run the generated JAR:
```bash
java -jar target/mediq-system-1.0.0.jar
```

6️⃣ **Access the application**

Open your browser and navigate to:
```
http://localhost:8080/mediq
```

**Default Credentials:**
- Admin: `admin@mediq.com` / `admin123`
- Doctor: `doctor@mediq.com` / `doctor123`
- Staff: `staff@mediq.com` / `staff123`

> ⚠️ **Important:** Change default passwords immediately after first login!

---

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=PatientServiceTest
```

### Generate test coverage report
```bash
mvn clean test jacoco:report
```

View the report at: `target/site/jacoco/index.html`

---

## 📦 Deployment

### Production Build

```bash
mvn clean package -DskipTests
```

### Deployment Options

#### 1. **Traditional Deployment (Tomcat)**
```bash
# Deploy the WAR file to Tomcat webapps directory
cp target/mediq-system.war $TOMCAT_HOME/webapps/
```

#### 2. **Docker Deployment**

Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/mediq-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t mediq-system .
docker run -p 8080:8080 mediq-system
```

#### 3. **Cloud Platforms**
- **AWS:** Deploy using Elastic Beanstalk or EC2
- **Azure:** Use Azure App Service
- **Google Cloud:** Deploy on App Engine
- **Heroku:** Use Heroku CLI for deployment

---

## 🤝 Contributing

We welcome contributions from the community! Here's how you can help:

### Contribution Workflow

1. **Fork** the repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit** your changes
   ```bash
   git commit -m "Add: amazing new feature"
   ```
4. **Push** to your branch
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open** a Pull Request

### Commit Message Convention

- `Add:` New feature
- `Fix:` Bug fix
- `Update:` Update existing feature
- `Refactor:` Code refactoring
- `Docs:` Documentation changes
- `Test:` Add or update tests

### Code Style Guidelines

- Follow Java naming conventions
- Write meaningful variable and method names
- Add comments for complex logic
- Ensure all tests pass before submitting PR
- Update documentation as needed

---

## 👥 Team

**Academic Project Team**
- **Group ID:** 2025-Y2-S1-MLB-B3G1-41
- **Project Type:** Web-Based Hospital Management System
- **Institution:** [Your Institution Name]

### Contributors
- Team Member 1 - Backend Developer
- Team Member 2 - Frontend Developer
- Team Member 3 - Database Administrator
- Team Member 4 - QA & Testing

*Want to join? Check out our [Contributing Guidelines](#-contributing)!*

---

## 📄 License

This project is developed for **educational purposes only** as part of an academic curriculum.

**Usage Restrictions:**
- Not for commercial use
- Not for production deployment without proper authorization
- Educational and learning purposes only

---

## 📞 Support & Contact

- 📧 **Email:** support@mediq-system.com
- 💬 **Issues:** [GitHub Issues](https://github.com/Ajitha-2001/MediQ_System/issues)
- 📖 **Documentation:** [Wiki](https://github.com/Ajitha-2001/MediQ_System/wiki)

---

## 🗺️ Roadmap

### Current Version (v1.0)
- ✅ Patient Management
- ✅ Doctor Management
- ✅ Appointment Scheduling
- ✅ Basic Reporting

### Upcoming Features (v2.0)
- 🔄 Billing & Payment Integration
- 🔄 Pharmacy Management
- 🔄 Lab Test Management
- 🔄 Mobile Application
- 🔄 Telemedicine Support
- 🔄 Advanced Analytics & AI Insights

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Bootstrap Documentation](https://getbootstrap.com/docs/)
- [REST API Best Practices](https://restfulapi.net/)

---

## ⭐ Acknowledgments

Special thanks to:
- Our academic supervisors and mentors
- Open source community
- All contributors and testers

---

<div align="center">

### ⭐ Star this repository if you find it helpful!

**Made with ❤️ by the MediQ Team**

[⬆ Back to Top](#mediq-system)

</div>
