package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenre(int id) {
        SqlRowSet genreRaw = jdbcTemplate.queryForRowSet("SELECT NAME FROM GENRE WHERE GENRE_ID = ?", id);
        if (genreRaw.next()) {
            return Genre.builder()
                    .name(genreRaw.getString("NAME"))
                    .id(id)
                    .build();
        } else {
            throw new NotFoundException("Genre not found");
        }
    }

    public List<Genre> getGenresList(int filmId) {
        List<Genre> genresList = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT GENRE_ID, NAME FROM GENRE g \n" +
                "JOIN FILM_GENRE fg ON G.GENRE_ID = FG.GENRE \n" +
                "WHERE FG.FILM_ID = ?", filmId);
        while (sqlRowSet.next()) {
            genresList.add(Genre.builder()
                    .id(sqlRowSet.getInt("GENRE_ID"))
                    .name(sqlRowSet.getString("NAME"))
                    .build());
        }
        return genresList;
    }

    public void setGenres(int filmId, List<Genre> genres) {
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ? ", filmId);
        if (genres != null) {
            for (Genre genre : genres) {
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT GENRE FROM FILM_GENRE WHERE FILM_ID = ?" +
                        "AND GENRE = ?", filmId, genre.getId());
                if (!sqlRowSet.next()) {
                    jdbcTemplate.update("INSERT INTO FILM_GENRE (FILM_ID, GENRE) VALUES (?, ?)", filmId, genre.getId());
                }
            }
        }
    }

    public List<Genre> getAllGenre() {
        List<Genre> genresList = new ArrayList<>();
        SqlRowSet genreRaw = jdbcTemplate.queryForRowSet("SELECT GENRE_ID FROM GENRE");
        while (genreRaw.next()) {
            genresList.add(getGenre(genreRaw.getInt("GENRE_ID")));
        }
        return genresList;
    }
}
