package ru.yandex.practicum.filmorate.storage.interfaces;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmStorageTest {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    User user = User.builder()
            .login("Evgeny")
            .birthday(LocalDate.of(1999, 5, 22))
            .name("Evgeny")
            .email("example@yandex.ru")
            .build();
    User user1 = User.builder()
            .login("Evgeny1")
            .birthday(LocalDate.of(1999, 5, 20))
            .name("Evgeny1")
            .email("example1@yandex.ru")
            .build();

    User user2 = User.builder()
            .login("Evgeny2")
            .birthday(LocalDate.of(1999, 5, 2))
            .email("example2@yandex.ru")
            .build();

    Mpa mpa1 = Mpa.builder()
            .id(1)
            .build();

    Mpa mpa2 = Mpa.builder()
            .id(2)
            .build();

    Mpa mpaWithName1 = Mpa.builder()
            .id(1)
            .name("G")
            .build();

    Mpa mpaWithName2 = Mpa.builder()
            .id(1)
            .name("G")
            .build();

    Genre genre1 = Genre.builder()
            .id(1)
            .build();

    Genre genre2 = Genre.builder()
            .id(2)
            .build();

    Genre genreWithName1 = Genre.builder()
            .id(1)
            .name("Комедия")
            .build();

    Genre genreWithName2 = Genre.builder()
            .id(2)
            .name("Драма")
            .build();

    Film film = Film.builder()
            .id(1)
            .name("The Man From Earth")
            .releaseDate(LocalDate.of(2007, 2, 14))
            .description("It stars David Lee Smith as John Oldman, a " +
                    "departing university professor, who puts forth the notion that he is more than 14,000 years old.")
            .duration(87)
            .mpa(mpa1)
            .genres(List.of(genre1))
            .build();

    Film film1 = Film.builder()
            .id(1)
            .duration(110)
            .name("Demolution man")
            .description("Stallone and Blade punch each other in the face")
            .releaseDate(LocalDate.of(1993, 8, 12))
            .mpa(mpa1)
            .genres(List.of(genre2))
            .build();

    @Test
    void createFilm() {
        filmDbStorage.createFilm(film);
        Film resultFilm = film.toBuilder().id(1).mpa(mpaWithName1).genres(List.of(genreWithName1)).build();
        Film getFilm = filmDbStorage.getFilm(1);
        Assertions.assertEquals(resultFilm, getFilm);
    }

    @Test
    void createFilmWith2Genres() {
        Film film2 = film.toBuilder().genres(List.of(genre1, genre2)).build();
        filmDbStorage.createFilm(film2);
        Film resultFilm = film.toBuilder().id(1).mpa(mpaWithName1).genres(
                List.of(genreWithName1, genreWithName2)).build();
        Film getFilm = filmDbStorage.getFilm(1);
        Assertions.assertEquals(resultFilm, getFilm);
    }

    @Test
    void updateFilm() {
        filmDbStorage.createFilm(film);
        Film resultFilm = film1.toBuilder().id(1).mpa(mpaWithName2).genres(
                List.of(genreWithName2)).build();
        filmDbStorage.updateFilm(resultFilm);
        Film getFilm = filmDbStorage.getFilm(1);
        Assertions.assertEquals(resultFilm, getFilm);
    }

    @Test
    void getAllFilm() {
        filmDbStorage.createFilm(film);
        filmDbStorage.createFilm(film1);
        Film resultFilm1 = film.toBuilder().id(1).mpa(mpaWithName1).genres(
                List.of(genreWithName1)).build();
        Film resultFilm2 = film1.toBuilder().id(2).mpa(mpaWithName2).genres(
                List.of(genreWithName2)).build();
        Assertions.assertEquals(List.of(resultFilm1, resultFilm2), filmDbStorage.getAllFilms());
    }

    @Test
    void filmNotFound() {
        NotFoundException notFoundException = Assertions.assertThrows(
                NotFoundException.class, () -> filmDbStorage.getFilm(1));
        Assertions.assertEquals("Film not found", notFoundException.getMessage());
    }

    @Test
    void likeAlreadyHas() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        User userAfterId = user.toBuilder().id(1).build();
        filmDbStorage.addLike(1, userAfterId);
        AlreadyExistException alreadyExistException = Assertions.assertThrows(
                AlreadyExistException.class, () -> filmDbStorage.addLike(1, userAfterId));
        Assertions.assertEquals("Лайк уже поставлен", alreadyExistException.getMessage());
    }

    @Test
    void removeUnknownLike() {
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);
        User userAfterId = user.toBuilder().id(1).build();
        NotFoundException notFoundException = Assertions.assertThrows(
                NotFoundException.class, () -> filmDbStorage.removeLike(1, userAfterId));
        Assertions.assertEquals("Лайк не найден", notFoundException.getMessage());
    }
}