package ru.yandex.practicum.filmorate.storage.interfaces;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {


    Film getFilm(Integer id);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film createFilm(Film film);
}
