package domus.challenge.model;

import java.util.List;

public class DirectorsResponse {
    private List<String> directors;

    public DirectorsResponse() {
    }

    public DirectorsResponse(List<String> directors) {
        this.directors = directors;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }
} 