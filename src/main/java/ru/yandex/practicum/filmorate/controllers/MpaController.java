package ru.yandex.practicum.filmorate.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.Valid;
import java.util.List;

@RestController
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "500", description = "Server error")
})
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaDao mpaDao;

    @Operation(summary = "Получение рейтинга")
    @ApiResponse(responseCode = "400", description = "Mpa not found")
    @GetMapping("/{id}")
    public Mpa getMpa(@Valid @PathVariable Integer id) {
        return mpaDao.getMpa(id);
    }

    @Operation(summary = "Получение всех рейтингов")
    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }
}
