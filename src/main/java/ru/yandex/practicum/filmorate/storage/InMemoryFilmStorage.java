package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film getFilm(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Film not found");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new NotFoundException("Film not found");
        }

    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new AlreadyExistException("Film already exist in storage");
        } else {
            films.put(film.getId(), film);
            return film;
        }
    }

    @Override
    public Film addLike(int filmId, User user) {
        Film filmToUpdate = getFilm(filmId);
        Set<Integer> users = filmToUpdate.getLikes();
        users.add(user.getId());
        return updateFilm(filmToUpdate.toBuilder().likes(users).build());
    }

    @Override
    public Film removeLike(int filmId, User user) {
        Film filmToUpdate = getFilm(filmId);
        Set<Integer> users = filmToUpdate.getLikes();
        users.remove(user.getId());
        return updateFilm(filmToUpdate.toBuilder().likes(users).build());
    }

    public int getCountLike(int filmId) {
        return getFilm(filmId).getLikes().size();
    }
}
