package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private int id = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.trace("Start create film");
        Film newFilm = film.toBuilder().id(getNextId()).build();
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Start update film");
        if (films.containsKey(film.getId())) {
            Film newFilm = films.get(film.getId())
                    .toBuilder()
                    .id(film.getId())
                    .name(film.getName())
                    .duration(film.getDuration())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .build();
            films.put(film.getId(), newFilm);
            log.info("Film update");
            return newFilm;
        } else {
            log.info("Film is not update");
            throw new ValidationException("Film not found");
        }
    }

    private int getNextId() {
        return id++;
    }
}
