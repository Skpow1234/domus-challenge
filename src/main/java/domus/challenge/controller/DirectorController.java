package domus.challenge.controller;

import domus.challenge.model.DirectorsResponse;
import domus.challenge.service.DirectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Directors", description = "API for retrieving directors based on movie count threshold")
public class DirectorController {

    private static final Logger log = LoggerFactory.getLogger(DirectorController.class);
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint", description = "Simple health check to verify the API is running")
    public Mono<ResponseEntity<Map<String, String>>> health() {
        return Mono.just(ResponseEntity.ok(Map.of("status", "UP", "message", "Domus Challenge API is running")));
    }

    @GetMapping("/directors")
    @Operation(
        summary = "Get directors with movie count above threshold",
        description = "Retrieves a list of directors who have directed more movies than the specified threshold. " +
                     "The results are sorted alphabetically. Negative threshold values return an empty list."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved directors",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DirectorsResponse.class),
                examples = @ExampleObject(
                    value = "{\"directors\": [\"Martin Scorsese\", \"Woody Allen\"]}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid threshold parameter",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Threshold must be a valid number\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"An error occurred while processing the request\"}"
                )
            )
        )
    })
    public Mono<ResponseEntity<DirectorsResponse>> getDirectorsAboveThreshold(
            @Parameter(
                description = "Minimum number of movies a director must have directed to be included in results",
                example = "4",
                required = true
            )
            @RequestParam("threshold") @Min(0) int threshold) {
        
        log.info("Received request for directors with threshold: {}", threshold);
        
        return directorService.getDirectorsAboveThreshold(threshold)
                .map(directors -> {
                    log.info("Found {} directors above threshold {}", directors.size(), threshold);
                    return ResponseEntity.ok(new DirectorsResponse(directors));
                })
                .onErrorResume(error -> {
                    log.error("Error processing directors request: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError()
                            .body(new DirectorsResponse(List.of())));
                });
    }
} 