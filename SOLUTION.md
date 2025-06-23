# Domus Challenge - Solution Documentation

## Overview

This solution implements a REST API that retrieves directors who have directed more movies than a specified threshold. The API fetches movie data from an external source, processes it to count movies per director, and returns directors sorted alphabetically.

## Architecture & Design Decisions

### 1. Technology Stack

- **Spring Boot 3.4.1** with Java 21
- **Spring WebFlux** for reactive programming (as preferred over RestTemplate)
- **WebClient** for HTTP client operations
- **Lombok** for reducing boilerplate code
- **SpringDoc OpenAPI** for Swagger documentation
- **Spring Validation** for input validation
- **Reactor Test** for testing reactive streams

### 2. Project Structure

```bash
src/main/java/domus/challenge/
├── ChallengeApplication.java          # Main application class
├── config/
│   └── WebClientConfig.java          # WebClient configuration
├── controller/
│   └── DirectorController.java       # REST endpoint controller
├── exception/
│   └── GlobalExceptionHandler.java   # Global error handling
├── model/
│   ├── DirectorsResponse.java        # API response model
│   ├── Movie.java                    # Movie entity
│   └── MovieApiResponse.java         # External API response
└── service/
    └── DirectorService.java          # Business logic service
```

## Implementation Details

### 1. External API Integration

- **Base URL**: `https://challenge.iugolabs.com`
- **Endpoint**: `/api/movies/search?page=<pageNumber>`
- **Pagination**: Intelligent handling of multiple pages with concurrent processing
- **Error Handling**: Graceful degradation when external API fails

### 2. Pagination Strategy

The solution implements an intelligent pagination approach:

- Fetches the first page to determine total pages
- Uses `Flux.range(1, totalPages)` to create a stream of page numbers
- Processes up to 5 pages concurrently using `flatMap(..., 5)`
- Collects all movies before processing director counts

### 3. Data Processing

- **Director Counting**: Groups movies by director and counts occurrences
- **Filtering**: Only includes directors with movie count > threshold
- **Sorting**: Returns directors in alphabetical order
- **Null Handling**: Filters out movies with null or empty director names

### 4. Error Handling & Validation

- **Input Validation**: `@Min(0)` ensures threshold is non-negative
- **Global Exception Handler**: Catches validation and general errors
- **Graceful Degradation**: Returns empty list on external API failures
- **Proper HTTP Status Codes**: 400 for bad requests, 500 for server errors

## API Endpoint

### GET `/api/directors?threshold=X`

**Parameters:**

- `threshold` (required): Minimum number of movies a director must have directed

**Response Format:**

```json
{
  "directors": ["Director Name 1", "Director Name 2"]
}
```

**Examples:**

- `GET /api/directors?threshold=4` → Returns directors with >4 movies
- `GET /api/directors?threshold=-1` → Returns empty list
- `GET /api/directors?threshold=abc` → Returns 400 Bad Request

## Testing Strategy

### 1. Unit Tests (`DirectorServiceTest`)

- Tests business logic in isolation
- Mocks external dependencies
- Covers edge cases (negative threshold, null directors, API errors)
- Tests sorting and filtering logic

### 2. Integration Tests (`DirectorControllerIntegrationTest`)

- Tests complete request/response flow
- Validates HTTP status codes and response format
- Tests validation and error handling
- Uses `@WebFluxTest` for reactive testing

### 3. Test Coverage

- Valid threshold scenarios
- Negative threshold handling
- Invalid input validation
- Error scenarios
- Empty result sets
- Alphabetical sorting

## Performance Considerations

### 1. Concurrent Processing

- Processes multiple API pages concurrently (up to 5 at a time)
- Reduces total response time for large datasets
- Uses reactive streams for non-blocking operations

### 2. Memory Efficiency

- Streams data instead of loading everything into memory
- Processes movies as they arrive from external API
- Efficient grouping and counting using Java streams

### 3. Caching Strategy

- No caching implemented (as per requirements)
- Each request fetches fresh data from external API
- Could be enhanced with caching for production use

## Security Considerations

### 1. Input Validation

- Validates threshold parameter to prevent injection attacks
- Uses Spring Validation framework
- Returns appropriate error messages

### 2. Error Information

- Generic error messages to avoid information leakage
- Proper logging for debugging without exposing sensitive data

## Monitoring & Observability

### 1. Logging

- Structured logging with SLF4J
- Logs request parameters and response counts
- Error logging for debugging external API issues

### 2. Metrics

- Could be enhanced with Micrometer for metrics collection
- Request/response timing
- External API call success/failure rates

## Deployment & Configuration

### 1. Application Properties

```properties
server.port=8080
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
logging.level.domus.challenge=INFO
```

### 2. Swagger Documentation

- Available at `/swagger-ui.html`
- Complete API documentation with examples
- Request/response schemas
- Error response documentation

## Future Enhancements

### 1. Caching

- Implement Redis caching for external API responses
- Cache director counts to reduce API calls
- TTL-based cache invalidation

### 2. Rate Limiting

- Implement rate limiting for external API calls
- Circuit breaker pattern for resilience
- Retry mechanism with exponential backoff

### 3. Monitoring

- Add health check endpoints
- Implement metrics collection
- Distributed tracing with Sleuth

### 4. Performance Optimization

- Implement pagination for large result sets
- Add database for persistent storage
- Background job processing for data updates

## Running the Application

### 1. Prerequisites

- Java 21
- Maven 3.6+

### 2. Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

### 3. Access Points

- **API**: `http://localhost:8080/api/directors?threshold=4`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api-docs`

### 4. Testing

```bash
mvn test
```

## Conclusion

This solution provides a robust, scalable, and well-tested implementation that meets all the challenge requirements. The use of modern Spring technologies (WebFlux, WebClient) ensures high performance and responsiveness, while comprehensive testing and documentation make it production-ready.
