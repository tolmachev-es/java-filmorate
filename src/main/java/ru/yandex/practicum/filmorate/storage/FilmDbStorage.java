package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Qualifier(value = "FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final LikeDao likeDao;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(MpaDao mpaDao, GenreDao genreDao, LikeDao likeDao, JdbcTemplate jdbcTemplate) {
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
        this.likeDao = likeDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilm(Integer id) {
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE FILM_ID = ?", id);
        if (filmRow.next()) {
            return Film.builder()
                    .id(id)
                    .name(filmRow.getString("TITLE"))
                    .description(filmRow.getString("DESCRIPTION"))
                    .releaseDate(filmRow.getDate("RELEASE_DATE").toLocalDate())
                    .duration(filmRow.getInt("DURATION_MINUTES"))
                    .rate(filmRow.getInt("RATE"))
                    .mpa(Objects.requireNonNull(mpaDao.getMpa(filmRow.getInt("MPA"))))
                    .genres(genreDao.getGenresList(id))
                    .build();
        } else {
            throw new NotFoundException("Film not found");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update("UPDATE FILM \n" +
                        " SET TITLE = ?, \n" +
                        "DESCRIPTION = ?, \n" +
                        "RELEASE_DATE = ?, \n" +
                        "DURATION_MINUTES = ?,\n" +
                        "RATE = ?,\n" +
                        "MPA = ?" +
                        "WHERE FILM_ID = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        genreDao.setGenres(film.getId(), film.getGenres());
        return getFilm(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmsRow = jdbcTemplate.queryForRowSet("SELECT FILM_ID FROM FILM ORDER BY FILM_ID");
        while (filmsRow.next()) {
            films.add(getFilm(filmsRow.getInt("film_id")));
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        jdbcTemplate.update("INSERT INTO FILM (TITLE, RELEASE_DATE, DESCRIPTION, DURATION_MINUTES, RATE, MPA) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId());
        SqlRowSet userRow = jdbcTemplate.queryForRowSet("SELECT MAX(FILM_ID) AS MAX_FILM_ID FROM FILM");
        userRow.next();
        genreDao.setGenres(userRow.getInt("MAX_FILM_ID"), film.getGenres());
        return getFilm(userRow.getInt("MAX_FILM_ID"));
    }

    @Override
    public Film addLike(int filmId, User user) {
        likeDao.addLike(filmId, user.getId());
        return getFilm(filmId);
    }

    @Override
    public Film removeLike(int filmId, User user) {
        likeDao.removeLike(filmId, user.getId());
        return getFilm(filmId);
    }

    public int getCountLike(int filmId) {
        return likeDao.getCountLike(filmId);
    }
}
