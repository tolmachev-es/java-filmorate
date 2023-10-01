package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class FilmController {
    private int id = 1;
    private Map<Integer, Film> films = new HashMap<>();

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film, BindingResult bindingResult){
        log.trace("Start create film");
        validationFilm(film, bindingResult);
        Film newFilm = film.toBuilder().id(getNextId()).build();
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @GetMapping("/films")
    public List<Film> getAllFilms(){
        return new ArrayList<>(films.values());
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult){
        log.info("Start update film");
        validationFilm(film, bindingResult);
        if(films.containsKey(film.getId())){
            Film newFilm = films.get(film.getId())
                    .toBuilder()
                    .id(film.getId())
                    .title(film.getTitle())
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

    private void validationFilm(Film film, BindingResult bindingResult){
        log.info("Start validation film");
        StringBuilder errorMsg = new StringBuilder(bindingResult.getFieldErrors()
                .stream()
                .map(s -> s.getField() + " " + s.getDefaultMessage())
                .collect(Collectors.joining(";")))
                .append((film.getReleaseDate().isBefore(Film.MIN_DATE)) ? "Release date less than min release date" : "");
        if(errorMsg.length() > 0){
            throw new ValidationException(errorMsg.toString());
        }
        log.info("Film is valid");
    }

    private int getNextId(){
        return id++;
    }
}
