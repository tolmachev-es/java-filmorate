package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private int filmId = 1;

    public Film createFilm(Film film) {
        return inMemoryFilmStorage.createFilm(film.toBuilder()
                .id(getNextId())
                .likes(new HashSet<>())
                .rate(film.getRate())
                .build());
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film getFilm(Integer id) {
        return inMemoryFilmStorage.getFilm(id);
    }

    public List<Film> getSortFilm(Integer count) {
        return inMemoryFilmStorage.getAllFilms().stream().sorted(new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return o2.getLikes().size() - o1.getLikes().size();
            }
        }).limit(count).collect(Collectors.toList());
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film filmToUpdate = inMemoryFilmStorage.getFilm(filmId);
        Set<Integer> users = filmToUpdate.getLikes();
        users.add(inMemoryUserStorage.getUser(userId).getId());
        return inMemoryFilmStorage.updateFilm(filmToUpdate.toBuilder().likes(users).build());
    }

    public Film removeLike(Integer filmId, Integer userId) {
        Film filmToUpdate = inMemoryFilmStorage.getFilm(filmId);
        Set<Integer> users = filmToUpdate.getLikes();
        users.remove(inMemoryUserStorage.getUser(userId).getId());
        return inMemoryFilmStorage.updateFilm(filmToUpdate.toBuilder().likes(users).build());
    }

    public Film updateFilm(Film film) {
        Film oldFilm = inMemoryFilmStorage.getFilm(film.getId());
        return inMemoryFilmStorage.updateFilm(film.toBuilder()
                .likes(oldFilm.getLikes())
                .build());
    }

    private int getNextId() {
        return filmId++;
    }
}
