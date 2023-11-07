package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

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

    public int getCountLike(int filmId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(USER_ID) AS COUNT FROM LIKES WHERE FILM_ID = ?",
                filmId);
        if (sqlRowSet.next()) {
            return sqlRowSet.getInt("COUNT");
        } else {
            return 0;
        }
    }

    private boolean hasLike(int filmId, int userId) {
        SqlRowSet like = jdbcTemplate.queryForRowSet("SELECT * FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?",
                filmId,
                userId);
        return like.next();
    }
}
