package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.domain.entities.Actor;
import dev.aj.sdj_hibernate.domain.entities.Movie;
import dev.aj.sdj_hibernate.domain.repositories.MovieRepository;
import dev.aj.sdj_hibernate.domain.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public Movie persistMovieAndActors(Movie movie, List<Actor> actors) {

        actors.forEach(actor -> {
            actor.addMovie(movie);
        });

        return movieRepository.saveAndFlush(movie);
    }

    @Override
    public Movie findAMovie() {
        return movieRepository.findAll().getFirst();
    }
}
