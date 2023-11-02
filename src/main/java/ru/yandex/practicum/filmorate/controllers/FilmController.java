package ru.yandex.practicum.filmorate.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "500", description = "Server error")
})
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Operation(summary = "Создание фильма")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Film not valid")
    })
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Start create film");
        return filmService.createFilm(film);
    }

    @Operation(summary = "Получение списка всех фильмов")
    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Start get all films");
        return filmService.getAllFilms();
    }

    @Operation(summary = "Обновление фильма")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Film not valid")
    })
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Start update film");
        return filmService.updateFilm(film);
    }

    @Operation(summary = "Получение фильма по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not found error")
    })
    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Integer filmId) {
        log.info("Start get film by id");
        return filmService.getFilm(filmId);
    }

    @Operation(summary = "Добавление лайка фильму")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Film or User not valid")
    })
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Start add Like");
        return filmService.addLike(id, userId);
    }

    @Operation(summary = "Удаление лайка с фильма")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not found error")
    })
    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Start remove Like");
        return filmService.removeLike(id, userId);
    }

    @Operation(summary = "Получение списка популярных фильмов")
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Start getting popular films");
        return filmService.getSortFilm(count);
    }
}
