package domus.challenge.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import domus.challenge.service.DirectorService;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@WebFluxTest(DirectorController.class)
class DirectorControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DirectorService directorService;

    @Test
    void getDirectorsAboveThreshold_WithValidThreshold_ReturnsDirectors() {
        // Given
        List<String> expectedDirectors = List.of("Martin Scorsese", "Woody Allen");
        when(directorService.getDirectorsAboveThreshold(4))
                .thenReturn(Mono.just(expectedDirectors));

        // When & Then
        webTestClient.get()
                .uri("/api/directors?threshold=4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors").isArray()
                .jsonPath("$.directors[0]").isEqualTo("Martin Scorsese")
                .jsonPath("$.directors[1]").isEqualTo("Woody Allen");
    }

    @Test
    void getDirectorsAboveThreshold_WithNegativeThreshold_ReturnsBadRequest() {
        // When & Then
        webTestClient.get()
                .uri("/api/directors?threshold=-1")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getDirectorsAboveThreshold_WithMissingThreshold_ReturnsBadRequest() {
        // When & Then
        webTestClient.get()
                .uri("/api/directors")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getDirectorsAboveThreshold_WithInvalidThreshold_ReturnsBadRequest() {
        // When & Then
        webTestClient.get()
                .uri("/api/directors?threshold=abc")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getDirectorsAboveThreshold_WithServiceError_ReturnsInternalServerError() {
        // Given
        when(directorService.getDirectorsAboveThreshold(anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient.get()
                .uri("/api/directors?threshold=1")
                .exchange()
                .expectStatus().is5xxServerError();
    }
} 