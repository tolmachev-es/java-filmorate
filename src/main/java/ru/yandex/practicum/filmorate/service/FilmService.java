package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    @NotNull
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    @NotNull
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film.toBuilder()
                .likes(new HashSet<>())
                .rate(film.getRate())
                .build());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getSortFilm(Integer count) {
        return filmStorage.getSortedFilm(count);
    }

    public Film addLike(Integer filmId, Integer userId) {
        return filmStorage.addLike(filmId, userStorage.getUser(userId));
    }

    public Film removeLike(Integer filmId, Integer userId) {
        return filmStorage.removeLike(filmId, userStorage.getUser(userId));
    }

    public Film updateFilm(Film film) {
        Film oldFilm = filmStorage.getFilm(film.getId());
        return filmStorage.updateFilm(film.toBuilder()
                .likes(oldFilm.getLikes())
                .build());
    }

}
