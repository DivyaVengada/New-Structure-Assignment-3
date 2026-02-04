
# Testing Strategy

## Approach
- Unit tests for all service classes using JUnit and Mockito.
- Mock repositories to isolate business logic.
- Test all API endpoints for expected responses and error handling.
- Aim for ≥80% code coverage.

## Coverage
- Purchase, authorize, capture, cancel, refund, subscription, webhook flows.
- Edge cases: invalid input, duplicate events, missing entities.

## Tools
- JUnit 5
- Mockito
- Spring Boot Test (for integration tests, if needed)
