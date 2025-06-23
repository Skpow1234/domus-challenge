package domus.challenge.service;

import domus.challenge.model.Movie;
import domus.challenge.model.MovieApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DirectorServiceTest {

    @Mock
    private WebClient webClient;

    private DirectorService directorService;

    @BeforeEach
    void setUp() {
        directorService = new DirectorService(webClient);
    }

    @Test
    void getDirectorsAboveThreshold_WithNegativeThreshold_ReturnsEmptyList() {
        // When
        Mono<List<String>> result = directorService.getDirectorsAboveThreshold(-1);

        // Then
        StepVerifier.create(result)
                .expectNext(List.of())
                .verifyComplete();
    }

    @Test
    void processDirectors_WithValidData_ReturnsFilteredDirectors() {
        // Given
        List<Movie> movies = List.of(
                createMovie("Movie1", "Director A"),
                createMovie("Movie2", "Director A"),
                createMovie("Movie3", "Director A"), // Director A has 3 movies
                createMovie("Movie4", "Director B"),
                createMovie("Movie5", "Director C")
        );

        // When
        List<String> result = directorService.processDirectors(movies, 2);

        // Then
        assertTrue(result.contains("Director A"));
        assertFalse(result.contains("Director B"));
        assertFalse(result.contains("Director C"));
    }

    @Test
    void processDirectors_WithMultipleDirectors_ReturnsSortedList() {
        // Given
        List<Movie> movies = List.of(
                createMovie("Movie1", "Zebra Director"),
                createMovie("Movie2", "Zebra Director"),
                createMovie("Movie3", "Zebra Director"),
                createMovie("Movie4", "Alpha Director"),
                createMovie("Movie5", "Alpha Director"),
                createMovie("Movie6", "Alpha Director")
        );

        // When
        List<String> result = directorService.processDirectors(movies, 2);

        // Then
        assertEquals(2, result.size());
        assertEquals("Alpha Director", result.get(0));
        assertEquals("Zebra Director", result.get(1));
    }

    @Test
    void processDirectors_WithNullDirectors_FiltersOutNullValues() {
        // Given
        List<Movie> movies = List.of(
                createMovie("Movie1", "Director A"),
                createMovie("Movie2", "Director A"),
                createMovie("Movie3", null),
                createMovie("Movie4", "")
        );

        // When
        List<String> result = directorService.processDirectors(movies, 1);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains("Director A"));
    }

    private Movie createMovie(String title, String director) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDirector(director);
        movie.setYear(2015);
        return movie;
    }
} 