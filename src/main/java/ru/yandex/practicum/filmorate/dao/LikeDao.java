package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Component
public class LikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(int filmId, int userId) {
        if (hasLike(filmId, userId)) {
            throw new AlreadyExistException("Лайк уже поставлен");
        } else {
            jdbcTemplate.update("INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)",
                    filmId,
                    userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        if (hasLike(filmId, userId)) {
            jdbcTemplate.update("DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?",
                    filmId,
                    userId);
        } else {
            throw new NotFoundException("Лайк не найден");
        }
    }

    public List<Integer> getCountFilmLike(int countFilms) {
        List<Integer> resultFilmId = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(
                "SELECT f.FILM_ID, COUNT(l.USER_ID) AS d FROM FILM f \n" +
                        "LEFT OUTER JOIN LIKES l ON f.FILM_ID = l.FILM_ID \n" +
                        "GROUP BY f.FILM_ID\n" +
                        "ORDER BY d DESC \n" +
                        "LIMIT ?", countFilms);
        while (sqlRowSet.next()) {
            resultFilmId.add(sqlRowSet.getInt("FILM_ID"));
        }
        return resultFilmId;
    }

    private boolean hasLike(int filmId, int userId) {
        SqlRowSet like = jdbcTemplate.queryForRowSet("SELECT * FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?",
                filmId,
                userId);
        return like.next();
    }
}
