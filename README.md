# Lending Application 



## Architecture Overview

```
com.lending.app/
├── common/           
├── product/          
├── customer/         
├── loan/             
└── notification/     
```

## Technologies

| Component | Choice | Version |
|-----------|--------|---------|
| Framework | Spring Boot | 3.3.5 |
| Java | Java LTS | 17 |
| Build | Maven | 3.9+ |
| Database | PostgreSQL (Docker) / H2 (test) | 16 / 2.x |
| ORM | Spring Data JPA / Hibernate | 6.x |
| Migrations | Flyway | 10.x |
| API Docs | Springdoc OpenAPI | 2.3.0 |
| Mapping | MapStruct | 1.5.5 |
| Testing | JUnit 5 + Mockito | 5.x |

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose (for PostgreSQL)



### Local Environment

```bash
# 1. Start PostgreSQL
docker-compose up -d postgres

# 2. Build the application
mvn clean package -DskipTests

# 3. Run the application
mvn spring-boot:run
```

### API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Testing with Swagger

The application is pre-loaded with seed data, so you can exercise the API immediately. Path parameters and request bodies in Swagger UI are pre-filled with example values from the seed data. Common IDs to try:

- **Customers**: 1–5
- **Products**: 1–3
- **Loans**: 1–8


## API Endpoints

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/products` | Create product |
| GET | `/api/v1/products` | List products |
| GET | `/api/v1/products/{id}` | Get product details |
| PUT | `/api/v1/products/{id}` | Update product |
| POST | `/api/v1/products/{id}/fees` | Add fee configuration |
| DELETE | `/api/v1/products/{id}/fees/{feeId}` | Remove fee |
| GET | `/api/v1/products/{id}/fees` | Get product fees |

### Customers
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/customers` | Register customer |
| GET | `/api/v1/customers` | List customers |
| GET | `/api/v1/customers/{id}` | Get customer profile |
| PUT | `/api/v1/customers/{id}` | Update customer |
| PUT | `/api/v1/customers/{id}/loan-limits` | Set loan limits |
| GET | `/api/v1/customers/{id}/loan-limit` | Get loan limit |
| PUT | `/api/v1/customers/{id}/billing-profile` | Update billing profile |
| GET | `/api/v1/customers/{id}/loans` | Get customer's loans |

### Loans
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/loans` | Create loan |
| POST | `/api/v1/loans/{id}/disburse` | Disburse loan |
| GET | `/api/v1/loans` | List loans |
| GET | `/api/v1/loans/{id}` | Get loan details |
| GET | `/api/v1/loans/{id}/installments` | Get installment schedule |
| GET | `/api/v1/loans/{id}/timeline` | Get loan audit history |
| GET | `/api/v1/loans/{id}/summary` | Get loan summary |
| POST | `/api/v1/loans/{id}/cancel` | Cancel loan |
| POST | `/api/v1/loans/{id}/write-off` | Write off loan |

### Repayments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/loans/{loanId}/repayments` | Make payment |
| GET | `/api/v1/loans/{loanId}/repayments` | Payment history |

### Notifications
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/customers/{customerId}/notifications` | Notification history |
| GET | `/api/v1/customers/{customerId}/notification-preferences` | Get preferences |
| PUT | `/api/v1/customers/{customerId}/notification-preferences` | Update preference |

## Design Decisions

### Domain-Driven Design (DDD)
- **Bounded Contexts**: Product, Customer, Loan, Notification
- **Value Objects**: `Money` (immutable, prevents currency bugs), `TenureConfig`, `IdempotencyKey`
- **Entities**: Aggregate roots with business logic (`Loan`, `Customer`, `LoanProduct`)
- **Domain Events**: Decoupled communication between contexts via Spring's `ApplicationEventPublisher`

### State Machine
- Encapsulates all valid loan lifecycle transitions
- Guard conditions prevent invalid operations (e.g., can't close loan with outstanding balance)
- States: `OPEN` -> `CLOSED` / `CANCELLED` / `OVERDUE` -> `WRITTEN_OFF`

### Strategy Pattern
- **Fee Calculation**: `FixedFeeStrategy`, `PercentageFeeStrategy`, `DailyAccrualStrategy`
- **Schedule Generation**: `LumpSumScheduleGenerator`, `InstallmentScheduleGenerator`
- **Notification Channels**: `EmailNotificationSender`, `SmsNotificationSender`, `PushNotificationSender`

### Event-Driven Architecture
- Domain events (`LoanCreatedEvent`, `PaymentReceivedEvent`, `LoanOverdueEvent`, etc.)
- Async event listeners for notifications
- Full audit trail via `LoanEventHistory`

### Loan Schedule Generation
- Strategy-based generation for Lump Sum and Installment loan types
- Installment loans support both DAYS and MONTHS tenure
- Proper remainder distribution on final installment

### Consolidated Billing
- `BillingProfile` under Customer with configurable billing day
- Multiple loans can share the same billing cycle

### Background Jobs
- `OverdueLoanSweepJob`: Daily cron job marking past-due loans as overdue
- `DailyFeeAccrualJob`: Daily fee accrual on active loans

### Idempotency
- Loan creation supports `Idempotency-Key` header
- Prevents duplicate loan disbursements
## Security

Authentication and Authorization are intentionally out of scope for this case study submission.

In a production system, this would be implemented using **Spring Security with JWT**, with the following structure:

- `ROLE_ADMIN` → full access to all endpoints
- `ROLE_LOAN_OFFICER` → loan disbursement and management
- `ROLE_VIEWER` → read-only access

Admin routes (`/api/v1/admin/**`) would be protected via role-based access control (RBAC), while customer-facing operations would require token-based authentication.

All monetary operations would additionally require audit logging tied to the authenticated principal.


## Testing

```bash
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest="*IntegrationTest"

# Run unit tests only
mvn test -Dtest="*Test" -DexcludedGroups=integration
```

### Test Pyramid
- **Unit Tests**: Domain logic (Money, StateMachine), Service layer
- **Integration Tests**: Full loan lifecycle (create -> disburse -> repay -> close)

## Database

### PostgreSQL (Production)
- Docker Compose setup included
- Flyway migrations for schema versioning
- Comprehensive seed data for demonstration

### H2 (Testing)
- In-memory database for tests
- Profile: `test`

## Seed Data

The application comes pre-loaded with:
- **3 Loan Products**: 30-day payday, 6-month installment, 12-month personal
- **5 Customers**: Varying credit profiles and loan limits
- **8 Loans**: All states (OPEN, CLOSED, OVERDUE, CANCELLED)
- **Notification Templates**: For all event types across all channels
- **Sample Repayment History**: Demonstrating payment flows

## Configuration

Key settings in `application.yml`:

| Property | Default | Description |
|----------|---------|-------------|
| `lending.jobs.overdue-sweep` | `0 0 1 * * ?` | Overdue detection cron |
| `lending.jobs.fee-accrual` | `0 30 1 * * ?` | Fee accrual cron |

## Project Structure

```
lending-app/
├── docker-compose.yml          # PostgreSQL + App setup
├── Dockerfile                  # Application container
├── pom.xml                    
├── README.md                  
└── src/
    ├── main/
    │   ├── java/com/lending/app/
    │   │   ├── LendingApplication.java
    │   │   ├── common/         # Shared kernel
    │   │   ├── product/        # Product context
    │   │   ├── customer/       # Customer context
    │   │   ├── loan/           # Loan management context
    │   │   └── notification/   # Notification context
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/   # Flyway SQL migrations
    └── test/                   # Unit & integration tests
```

## Deployment

### Render 

A `render.yaml` Blueprint is included for one-click deployment.

1. Sign in to [Render](https://render.com) and click **New + → Blueprint**.
2. Connect your GitHub repo and deploy.
3. Render will create a free PostgreSQL database and build the Docker image.


The app reads the `DATABASE_URL` environment variable provided by Render and converts it to Spring Boot datasource properties automatically.

### Manual Docker

```bash
mvn clean package -DskipTests
docker build -t lending-app .
docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=... lending-app
```
