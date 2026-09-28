# 📦 Hostel ComplaintBox - Maintenance Tracking System

A full-stack Spring Boot web application for managing and tracking hostel maintenance complaints.

---

## 🌟 Key Features

1. **Resident Self-Service**: Submit maintenance complaints specifying category, room number, and description.
2. **Staff & Warden Management**: Assign maintenance technicians to open issues, track progress, and log resolution remarks.
3. **Workflow & Status Progression**:
   - `OPEN` ➔ `IN_PROGRESS` ➔ `RESOLVED`
   - Enforces strict unidirectional status transitions to avoid regression.
4. **Warden Overdue Monitoring**:
   - Dynamic tracking of complaints that have been open for more than **5 days**.
   - Sort open issues by complaint age (oldest first).
5. **Modern White Theme UI**:
   - Clean Thymeleaf-powered dashboard with real-time summary statistics, status badges, and management panels.
6. **REST API & Swagger-Ready Design**:
   - Full JSON REST APIs with Jakarta validation and centralized global exception handling (`@RestControllerAdvice`).

---

## 🛠️ Tech Stack

* **Backend**: Java 21, Spring Boot 4.1.x, Spring Data JPA, Hibernate, Jakarta Validation
* **Frontend**: Thymeleaf, HTML5, CSS3 (Custom White Theme)
* **Database**: MySQL
* **Build Tool**: Maven

---

## 📋 Database Entities

* **Resident**: `id`, `name`, `email`, `roomNumber`
* **Category**: `id`, `name`, `description`
* **Staff**: `id`, `name`, `email`, `role` (`STAFF` or `WARDEN`)
* **Complaint**: `id`, `roomNumber`, `description`, `status`, `createdAt`, `resolvedAt`, `overdue`, `resolutionRemark`

---

## 🚀 Getting Started

### 1. Database Configuration
Update MySQL credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/complaint_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 2. Run the Application
```bash
./mvnw spring-boot:run
```

### 3. Open in Browser
Visit: [http://localhost:8080/](http://localhost:8080/)
