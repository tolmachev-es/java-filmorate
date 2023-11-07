package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpa(int rateId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT NAME FROM RATING WHERE RATE_ID = ?", rateId);
        if (sqlRowSet.next()) {
            return Mpa.builder()
                    .id(rateId)
                    .name(sqlRowSet.getString("NAME"))
                    .build();
        } else {
            throw new NotFoundException("Rating not found");
        }
    }

    public List<Mpa> getAllMpa() {
        List<Mpa> allMpa = new ArrayList<>();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING");
        while (mpaRows.next()) {
            allMpa.add(Mpa.builder()
                    .id(mpaRows.getInt("RATE_ID"))
                    .name(mpaRows.getString("NAME"))
                    .build());
        }
        return allMpa;
    }
}
