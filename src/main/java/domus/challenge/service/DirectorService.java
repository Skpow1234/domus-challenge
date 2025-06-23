package domus.challenge.service;

import domus.challenge.model.Movie;
import domus.challenge.model.MovieApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DirectorService {

    private static final Logger log = LoggerFactory.getLogger(DirectorService.class);
    private final WebClient webClient;
    private static final String EXTERNAL_API_BASE_URL = "https://challenge.iugolabs.com/api/movies/search";

    public DirectorService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<String>> getDirectorsAboveThreshold(int threshold) {
        if (threshold < 0) {
            return Mono.just(List.of());
        }

        return getFirstPage()
                .flatMapMany(firstPage -> {
                    int totalPages = firstPage.getTotal_pages();
                    log.info("Total pages to fetch: {}", totalPages);
                    
                    // Create a flux of all pages starting from page 1
                    return Flux.range(1, totalPages)
                            .flatMap(this::fetchMoviesPage, 5) // Process 5 pages concurrently
                            .flatMapIterable(MovieApiResponse::getData);
                })
                .collectList()
                .map(movies -> processDirectors(movies, threshold));
    }

    private Mono<MovieApiResponse> getFirstPage() {
        return fetchMoviesPage(1);
    }

    private Mono<MovieApiResponse> fetchMoviesPage(int pageNumber) {
        log.debug("Fetching page: {}", pageNumber);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/movies/search")
                        .queryParam("page", pageNumber)
                        .build())
                .retrieve()
                .bodyToMono(MovieApiResponse.class)
                .doOnError(error -> log.error("Error fetching page {}: {}", pageNumber, error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Failed to fetch page {}, returning empty response", pageNumber);
                    return Mono.empty();
                });
    }

    public List<String> processDirectors(List<Movie> movies, int threshold) {
        // Count movies per director
        Map<String, Long> directorCounts = movies.stream()
                .filter(movie -> movie.getDirector() != null && !movie.getDirector().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        Movie::getDirector,
                        Collectors.counting()
                ));

        // Filter directors with count > threshold and sort alphabetically
        return directorCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }
} 