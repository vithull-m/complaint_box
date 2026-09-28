# Hostel ComplaintBox Project Report

## 1. Abstract

Hostel ComplaintBox is a web-based maintenance complaint tracking system built with Java and Spring Boot. It gives residents a way to submit maintenance issues and gives hostel staff and wardens a shared workflow for reviewing, assigning, and resolving them. Complaints are associated with a resident and maintenance category, move through a defined status lifecycle, and can be monitored for age and overdue status. The application provides both a Thymeleaf web interface and JSON REST endpoints, with MySQL persistence through Spring Data JPA.

## 2. Introduction

In a hostel, maintenance requests can be difficult to track when they are reported informally. Requests may be missed, their ownership may be unclear, and residents may not know whether work has started. Hostel ComplaintBox centralizes complaint records and makes their status, category, assignment, and resolution information available through one application.

The project is intended as a small, maintainable system for organizing this workflow. It is not currently an authenticated or production-hardened service; those limitations are described below.

## 3. Objectives

- Provide a structured way to record a resident's maintenance complaint.
- Organize complaints by maintenance category and room number.
- Allow staff to be assigned and complaint progress to be tracked.
- Apply a consistent status lifecycle: `OPEN`, `IN_PROGRESS`, and `RESOLVED`.
- Identify unresolved complaints older than five days and display operational summary counts.
- Provide both browser-based pages and REST endpoints for managing records.

## 4. Scope and Main Functions

The application currently supports:

- A dashboard listing complaints and summary counts for total, open, in-progress, resolved, and overdue complaints.
- Complaint creation, detail viewing, assignment, status updates, and deletion.
- Resident, category, and staff administration through the web interface and REST API.
- A REST API for complaint, resident, category, and staff records.
- Input validation and centralized JSON error responses for REST requests.
- Resolution timestamps and optional resolution remarks.

The project does not currently include user login, a role-based web security layer, email or push notifications, file attachments, or a dedicated Swagger/OpenAPI dependency.

## 5. Technology Stack

| Area | Technology |
|---|---|
| Language/runtime | Java 21 |
| Application framework | Spring Boot 4.1.1 |
| Web | Spring MVC and Thymeleaf |
| Persistence | Spring Data JPA and Hibernate |
| Database | MySQL |
| Validation | Jakarta Validation |
| Build | Maven and Maven Wrapper |
| Tests | JUnit 5 and Spring Boot test dependencies |

## 6. System Architecture

The application follows a layered structure:

1. **Presentation layer:** `WebViewController` serves Thymeleaf pages; REST controllers expose JSON endpoints.
2. **Service layer:** services implement complaint and record-management operations. `ComplaintService` contains complaint workflow, assignment, overdue calculation, and transition checks.
3. **Persistence layer:** Spring Data repositories read and write the JPA entities.
4. **Database layer:** MySQL stores residents, categories, staff, and complaints.

REST-specific errors are converted by a centralized exception handler into responses containing a timestamp, HTTP status, error label, message, and request path.

## 7. Data Model

| Entity | Main information | Relationships |
|---|---|---|
| `Resident` | Name, email, room number | Can be associated with complaints |
| `Category` | Name and description | Can be associated with complaints |
| `Staff` | Name, email, role (`STAFF` or `WARDEN`) | May be assigned to complaints |
| `Complaint` | Room, description, status, creation/resolution times, overdue flag, resolution remark | References one resident and category; may reference assigned staff |

Complaint status is stored as an enum string. Complaint creation initializes its status as `OPEN` and records the creation time.

## 8. Complaint Workflow and Business Rules

The normal workflow is:

`OPEN` -> `IN_PROGRESS` -> `RESOLVED`

Assigning staff to an `OPEN` complaint moves it to `IN_PROGRESS`. A resolved complaint cannot be changed to another status, and moving an in-progress complaint back to open is rejected. The current service does allow an `OPEN` complaint to move directly to `RESOLVED`; strict step-by-step progression is therefore not fully enforced. When a complaint is resolved, the service records the resolution time and supplied remark and clears its overdue flag.

An unresolved complaint is considered overdue after it is more than five days old. The overdue value is refreshed when complaints are retrieved; the overdue query excludes resolved complaints. Open complaints can also be listed oldest first.

## 9. REST API Overview

All routes are rooted at `/api`.

| Resource | Available operations |
|---|---|
| `/complaints` | `POST` create; `GET /{id}` retrieve; `DELETE /{id}` delete |
| `/complaints/resident/{residentId}` | `GET` complaints for a resident |
| `/complaints/open` | `GET` open complaints ordered by creation time |
| `/complaints/overdue` | `GET` overdue unresolved complaints |
| `/complaints/{id}/assign/{staffId}` | `PUT` assign staff |
| `/complaints/{id}/status` | `PUT` update status |
| `/residents` | `GET`, `POST`; `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| `/categories` | `GET`, `POST`; `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| `/staff` | `GET`, `POST`; `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |

Create and update request bodies use validation where configured. Successful creates return HTTP `201`; deletes return HTTP `204`. The source includes no Swagger/OpenAPI dependency, so these routes are not currently published through an interactive API documentation page.

## 10. Web Interface

The Thymeleaf interface provides a dashboard and pages for complaint creation and details, resident management, category management, and staff management. Dashboard filters include all complaints, open complaints, and overdue complaints. The complaint detail page supports staff assignment and status changes.

## 11. Setup and Execution

### Prerequisites

- JDK 21
- MySQL server
- A database account with permission to create or update the configured schema

### Database configuration

Configure the JDBC URL and database credentials in `src/main/resources/application.properties` for the local environment. Do not commit real credentials; use environment variables or an untracked local configuration file for secrets. The current JPA setting is `ddl-auto=update`, which lets Hibernate update the schema during development.

### Run on Windows

```powershell
./mvnw.cmd test
./mvnw.cmd spring-boot:run
```

After startup, open `http://localhost:8080/` for the web application. The configured server port is `8080`.

## 12. Testing and Current Limitations

The checked-in test suite contains a Spring application context-load test. It does not currently exercise complaint creation, status transitions, overdue calculation, REST responses, or database behavior with dedicated tests. The context test also depends on the application environment being able to initialize its configured data source.

Other limitations to address before production use:

- There is no authentication or Spring Security configuration. Complaint resolution checks use an optional `actingStaffId`; when that value is omitted, the service does not verify the caller's identity. This is not a secure authorization boundary.
- Status validation does not require an `OPEN` complaint to pass through `IN_PROGRESS` before resolution.
- Database credentials are configured directly in the application properties file and should be moved to secret-managed environment configuration. Rotate any credential that has been shared outside the local development environment.
- `ddl-auto=update` and SQL logging are development-oriented settings; production deployment should use managed schema migrations and reviewed logging settings.
- The project has no pagination, audit history, notification mechanism, or API documentation dependency at present.

## 13. Future Enhancements

- Add authentication and enforce staff/warden permissions using the authenticated principal.
- Add focused unit and integration tests for workflow rules, validation, REST endpoints, and persistence.
- Move credentials to environment variables or a secret manager, and adopt a migration tool such as Flyway.
- Add complaint history/audit events and notifications for assignment, progress, and resolution.
- Add pagination and filtering for larger complaint lists.
- Add OpenAPI documentation if API consumers need an interactive specification.

## 14. Conclusion

Hostel ComplaintBox provides a useful foundation for digitizing hostel maintenance requests. Its Java/Spring architecture separates web handling, business logic, and persistence, while its complaint workflow supports assignment, progress tracking, and overdue monitoring. The current project is suitable as a development or academic prototype. Authentication, stronger authorization, externalized secrets, broader automated tests, and production database migration practices are the main steps needed before operational deployment.