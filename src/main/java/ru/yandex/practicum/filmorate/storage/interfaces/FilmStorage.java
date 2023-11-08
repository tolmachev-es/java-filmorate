package ru.yandex.practicum.filmorate.storage.interfaces;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {

    Film getFilm(Integer id);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film addLike(int filmId, User user);

    Film removeLike(int filmId, User user);

    List<Film> getSortedFilm(int count);
}
