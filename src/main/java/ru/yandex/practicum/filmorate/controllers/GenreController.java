package ru.yandex.practicum.filmorate.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import javax.validation.Valid;
import java.util.List;

@RestController
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "500", description = "Server error")
})
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreDao genreDao;

    @Operation(summary = "Получение жанра")
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public Genre getGenre(@Valid @PathVariable Integer id) {
        return genreDao.getGenre(id);
    }

    @Operation(summary = "Получение всех жанров")
    @GetMapping
    public List<Genre> getAllGenre() {
        return genreDao.getAllGenre();
    }
}
