package domus.challenge.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebInputException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(WebExchangeBindException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid threshold parameter. Threshold must be a non-negative number.");
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid threshold parameter. Threshold must be a non-negative number.");
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestValueException(MissingRequestValueException ex) {
        log.warn("Missing request value: {}", ex.getMessage());
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Required query parameter 'threshold' is not present.");
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Map<String, String>> handleServerWebInputException(ServerWebInputException ex) {
        log.warn("Server web input error: {}", ex.getMessage());
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid threshold parameter. Threshold must be a valid number.");
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFoundException(NoResourceFoundException ex) {
        // Don't log 404 errors for static resources like Swagger UI and favicon
        if (ex.getMessage().contains("swagger-ui.html") || 
            ex.getMessage().contains("favicon.ico") || 
            ex.getMessage().contains("api-docs")) {
            return ResponseEntity.notFound().build();
        }
        
        log.warn("Resource not found: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Resource not found");
        
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "An error occurred while processing the request");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 