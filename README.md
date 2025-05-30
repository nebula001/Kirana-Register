# Kirana Register API

## Overview

Kirana Register is a Spring Boot application designed to manage financial transactions and generate reports for users.
It supports transaction creation, retrieval, and reporting (weekly, monthly, yearly) with integration to Apache Kafka
for asynchronous report processing. The application uses Spring Security with JWT-based authentication for secure
access, PostgreSQL for persistent data storage, and Redis for caching and rate limiting. Additionally, it integrates
with an external currency conversion API to handle multi-currency transactions.

## Tech Stack

- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Messaging**: Apache Kafka
- **Caching & Rate Limiting**: Redis
- **Security**: Spring Security with JWT authentication
- **Validation**: Hibernate Validator
- **ORM**: Spring Data JPA
- **External API**: Currency conversion via `fxratesapi.com`

## Features

- **User Authentication**:
    - Register and login endpoints with JWT token generation.
    - Role-based access control (USER and ADMIN roles).
    - Logout functionality to invalidate sessions.
- **Transaction Management**:
    - Create and retrieve transactions for authenticated users.
    - Support for multiple currencies (USD, INR) with real-time conversion using an external API.
    - Transaction types (CREDIT, DEBIT).
- **Reporting**:
    - Generate weekly, monthly, and yearly reports for users and admins.
    - User-specific reports based on transaction history.
    - Admin reports for aggregated data.
    - Kafka integration for asynchronous report generation.
- **Currency Conversion**:
    - Fetches real-time exchange rates for USD to INR conversions.
    - Caches exchange rates in Redis for performance.
- **Rate Limiting**:
    - Limits API requests to 10 per minute per user or IP using Redis.
- **Health Check**: Endpoint to verify API availability.
- **Security**: JWT-based authentication and authorization.
- **Caching**: Redis-based caching for exchange rates and other data.

## Project Structure

The application follows a modular structure with distinct packages for controllers, services, entities, DTOs, and
configurations.

### Key Files

- **Controllers**:
    - `AuthController.java`: Handles user registration, login, logout, and health checks.
    - `TransactionController.java`: Manages transaction creation and retrieval for authenticated users.
    - `ReportController.java`: Provides admin endpoints for generating weekly, monthly, and yearly reports.
    - `ReportControllerUser.java`: Generates user-specific reports.
    - `KafkaReportController.java`: Sends reports to Kafka topics for asynchronous processing.
- **Entities**:
    - `Users.java`: Represents user data with email, username, password, and role.
    - `Transaction.java`: Defines transaction details including amount, currency, type, and associated user.
    - `Role.java`: Enum for user roles (USER, ADMIN).
    - `TransactionType.java`: Enum for transaction types (CREDIT, DEBIT).
    - `Currency.java`: Enum for supported currencies (USD, INR).
- **DTOs**:
    - `RegisterRequestDTO.java`: Input for user registration (email, username, password).
    - `RegisterResponseDTO.java`: Response for registration with user details.
    - `LoginRequestDTO.java`: Input for login (email, password).
    - `LoginResponseDTO.java`: Response for login with JWT token.
    - `TransactionDTO.java`: Represents transaction data for API requests/responses.
    - `ReportResponseDTO.java`: Represents report data with total credits, debits, and net flow in USD and INR.
    - `CurrencyDTO.java`: Represents amounts in USD and INR.
    - `ExchangeApiDTO.java`: Maps external currency API response for exchange rates.
- **Services**:
    - `CurrencyConversionService.java`: Fetches and caches exchange rates from an external API.
- **Configurations**:
    - `RateLimitFilter.java`: Implements rate limiting using Redis.
    - `application.yml`: Configures Spring Boot, PostgreSQL, Redis, Kafka, and JWT settings.

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL (version 13 or higher)
- Redis (version 6 or higher)
- Apache Kafka (version 2.8 or higher)
- Internet access for external currency API (`https://api.fxratesapi.com`)

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/nebula001/Kirana-Register
   cd kirana-register
   ```

2. **Set Up PostgreSQL**:
    - Create a database named `Kirana`.
    - Update `application.yml` with your PostgreSQL credentials if different from defaults (`username: postgres`,
      `password: password`).

3. **Set Up Redis**:
    - Ensure Redis is running on `localhost:6379` or update `application.yml` with your Redis configuration.

4. **Set Up Kafka**:
    - Ensure Kafka is running on `localhost:9092`.
    - Create Kafka topics `string-reports-topic` and `json-reports-topic`:
      ```bash
      kafka-topics.sh --create --topic string-reports-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
      ```

5. **Configure Application**:
    - Verify or update `application.yml` for database, Redis, Kafka, and JWT settings.
    - The JWT secret is preconfigured, but you can generate a new 256-bit secret for HS256 algorithm if needed.
    - Ensure internet access for the currency conversion API.

6. **Build and Run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## Configuration

The `application.yml` file contains the following key configurations:

- **Spring MVC**: Throws exceptions for unmapped endpoints and disables static resource mappings.
- **DataSource**: Connects to PostgreSQL at `jdbc:postgresql://localhost:5433/Kirana`.
- **Redis**: Configured at `localhost:6379` with a cache TTL of 10 minutes.
- **JPA**: Uses `hibernate.ddl-auto=update` for schema updates and enables SQL logging.
- **Kafka**: Connects to `localhost:9092` with JSON and string serializers for producers and deserializers for
  consumers.
- **JWT**: Configured with a 256-bit secret and 24-hour token expiration.
- **Kafka Topics**:
    - `string-reports-topic`: For string-based report data.

## API Endpoints

### Authentication

- **POST /api/auth/register**
    - Registers a new user.
    - Request Body: `RegisterRequestDTO` (email, username, password).
    - Response: `RegisterResponseDTO` (id, email, username, role) (HTTP 201).
- **POST /api/auth/login**
    - Authenticates a user and returns a JWT token.
    - Request Body: `LoginRequestDTO` (email, password).
    - Response: `LoginResponseDTO` (message, token) (HTTP 200).
- **GET /api/auth/logout**
    - Invalidates the user session.
    - Response: Success message (HTTP 200).
- **GET /api/auth/health-check**
    - Verifies API availability.
    - Response: "Health check passed!" (HTTP 200).

### Transactions

- **POST /api/transactions**
    - Creates a new transaction for the authenticated user.
    - Request Body: `TransactionDTO` (amount, currency, type).
    - Response: `TransactionDTO` (id, amount, amountUsd, amountInr, type, currency, transactionDate, userId) (HTTP 201).
    - Authentication: JWT required.
- **GET /api/transactions**
    - Retrieves all transactions for the authenticated user.
    - Response: List of `TransactionDTO` (HTTP 200).
    - Authentication: JWT required.

### User Reports

- **GET /api/reports/weekly**
    - Generates a weekly report for the authenticated user.
    - Response: `ReportResponseDTO` (reportType, totalCredits, totalDebits, netFlow, startDate, endDate) (HTTP 200).
    - Authentication: JWT required.
- **GET /api/reports/monthly**
    - Generates a monthly report for the authenticated user.
    - Response: `ReportResponseDTO` (HTTP 200).
    - Authentication: JWT required.
- **GET /api/reports/yearly**
    - Generates a yearly report for the authenticated user.
    - Response: `ReportResponseDTO` (HTTP 200).
    - Authentication: JWT required.

### Admin Reports

- **GET /api/admin/reports/weekly**
    - Generates a weekly report for all users (admin only).
    - Response: `ReportResponseDTO` (HTTP 200).
    - Authentication: JWT required (ADMIN role).
- **GET /api/admin/reports/monthly**
    - Generates a monthly report for all users (admin only).
    - Response: `ReportResponseDTO` (HTTP 200).
    - Authentication: JWT required (ADMIN role).
- **GET /api/admin/reports/yearly**
    - Generates a yearly report for all users (admin only).
    - Response: `ReportResponseDTO` (HTTP 200).
    - Authentication: JWT required (ADMIN role).

### Kafka Reports

- **POST /api/kafka/reports/weekly**
    - Sends a weekly report to Kafka for asynchronous processing.
    - Response: "Weekly report sent to Kafka" (HTTP 200).
- **POST /api/kafka/reports/monthly**
    - Sends a monthly report to Kafka for asynchronous processing.
    - Response: "Monthly report sent to Kafka" (HTTP 200).
- **POST /api/kafka/reports/yearly**
    - Sends a yearly report to Kafka for asynchronous processing.
    - Response: "Yearly report sent to Kafka" (HTTP 200).

## Security

- **JWT Authentication**: All endpoints except `/api/auth/register`, `/api/auth/login`, and `/api/auth/health-check`
  require a valid JWT token in the `Authorization` header (Bearer token format).
- **Role-Based Access**: Admin endpoints (`/api/admin/*`) require the `ADMIN` role. User-specific endpoints (
  `/api/transactions`, `/api/reports/*`) are accessible to authenticated users with the `USER` or `ADMIN` role.
- **Rate Limiting**: Implemented via `RateLimitFilter` using Redis, limiting users or IPs to 10 requests per minute.
- **Validation**: Input validation is enforced using `@Valid` annotations and Hibernate Validator constraints (e.g.,
  `@NotBlank`, `@Email`, `@DecimalMin`).

## Database Schema

- **Users Table**:
    - `id`: Primary key (auto-incremented).
    - `email`: Unique, non-null.
    - `username`: Non-null.
    - `password`: Non-null (hashed).
    - `role`: Enum (USER or ADMIN).
- **Transactions Table**:
    - `id`: Primary key (auto-incremented).
    - `amount`: Decimal, non-null, minimum 0.01.
    - `amount_usd`: Decimal (converted amount in USD).
    - `amount_inr`: Decimal (converted amount in INR).
    - `type`: Enum (CREDIT or DEBIT).
    - `currency`: Enum (USD or INR).
    - `transaction_date`: Timestamp, non-null.
    - `user_id`: Foreign key referencing `Users(id)`.

## Kafka Integration

- **Topics**:
    - `string-reports-topic`: For string-based report data.

- **Producer**: Uses `JsonSerializer` for JSON data and `StringSerializer` for string data.
- **Consumer**: Configured with `report-group` group ID and `JsonDeserializer` for JSON data.

## Caching

- **Redis Cache**:
    - Used for caching exchange rates in `CurrencyConversionService` (cache name: `externalApiCache`).
    - Default TTL of 10 minutes for cached data.
- **Rate Limiting**:
    - Uses Redis to track request counts per user or IP.
    - Keys: `rate_limit:user:<userId>` or `rate_limit:ip:<ipAddress>`.
    - TTL: 60 seconds.

## Currency Conversion

- **Service**: `CurrencyConversionService` fetches exchange rates from `https://api.fxratesapi.com/latest`.
- **DTO**: `ExchangeApiDTO` maps the API response, extracting the INR rate.
- **Caching**: Exchange rates are cached in Redis to reduce external API calls.
- **Error Handling**:
    - Throws `ResourceNotFoundException` if INR rate is unavailable.
    - Throws `ExternalServiceException` for API failures.

## Data Transfer Objects (DTOs)

- **RegisterRequestDTO**: Validates user registration input (email, username, password).
- **RegisterResponseDTO**: Returns user details (id, email, username, role) after registration.
- **LoginRequestDTO**: Validates login input (email, password).
- **LoginResponseDTO**: Returns JWT token and message after login.
- **TransactionDTO**: Represents transaction data with amount, converted amounts (USD, INR), type, currency, and user
  ID.
- **ReportResponseDTO**: Contains report details (type, total credits/debits/net flow in USD and INR, date range).
- **CurrencyDTO**: Represents amounts in USD and INR for transactions.
- **ExchangeApiDTO**: Maps external API response for currency conversion rates.

## Testing

**Postman**:

- Create a Postman collection to test endpoints.
- Include JWT token in headers for protected endpoints.
- Test rate limiting by sending >10 requests per minute.

## Troubleshooting

- **Database Connection Issues**: Verify PostgreSQL is running and credentials match `application.yml`.
- **Kafka Errors**: Ensure Kafka server is running and topics are created.
- **Redis Issues**: Check Redis server status and port configuration.
- **JWT Issues**: Ensure the JWT secret is valid and tokens are not expired.
- **Currency API Issues**: Verify internet connectivity and API availability (`https://api.fxratesapi.com`).
- **Rate Limiting Errors**: If `429 Too Many Requests` is received, wait 60 seconds or check Redis configuration.
