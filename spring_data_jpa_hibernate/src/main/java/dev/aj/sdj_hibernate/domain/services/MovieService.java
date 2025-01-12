package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.Actor;
import dev.aj.sdj_hibernate.domain.entities.Movie;

import java.util.List;

public interface MovieService {

    Movie persistMovieAndActors(Movie movie, List<Actor> actors);

    Movie findAMovie();
}
