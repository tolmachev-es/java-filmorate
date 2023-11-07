package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class FilmControllerTest {
    @Autowired
    MockMvc mockMvc;
    Mpa mpa = Mpa.builder()
            .id(1)
            .build();

    Mpa mpa1 = Mpa.builder()
            .id(1)
            .name("G")
            .build();
    Film film = Film.builder()
            .id(1)
            .name("The Man From Earth")
            .releaseDate(LocalDate.of(2007, 2, 14))
            .description("It stars David Lee Smith as John Oldman, a " +
                    "departing university professor, who puts forth the notion that he is more than 14,000 years old.")
            .duration(87)
            .mpa(mpa)
            .genres(new ArrayList<>())
            .build();
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createFilmNameBlank() throws Exception {
        Film newFilm = film.toBuilder().name("").build();
        postToFilmBadRequest(newFilm, "Title can not be empty");
    }

    @Test
    void createFilmDescriptionMoreThan200Length() throws Exception {
        Film newFilm = film.toBuilder().description(film.getDescription() + film.getDescription()).build();
        postToFilmBadRequest(newFilm, "Description should be less than 200 length");
    }

    @Test
    void createFilmIncorrectDate() throws Exception {
        Film newFilm = film.toBuilder().releaseDate(LocalDate.MIN).build();
        postToFilmBadRequest(newFilm, "Release date less than min release date");
    }

    @Test
    void createFilmIncorrectDuration() throws Exception {
        Film newFilm = film.toBuilder().duration(-1).build();
        postToFilmBadRequest(newFilm, "Duration should be greater than 0");
    }

    @Test
    void createNewFilm() throws Exception {
        Film newFilm = film.toBuilder().build();
        ResultActions ra = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFilm)).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, ra.andReturn().getResponse().getStatus());
        Film resultFilm = film.toBuilder().id(1).mpa(mpa1).build();
        ResultActions getAllTask = mockMvc.perform(MockMvcRequestBuilders.get("/films")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(objectMapper.writeValueAsString(new ArrayList<>(Collections.singleton(resultFilm))),
                getAllTask.andReturn().getResponse().getContentAsString());
    }

    @Test
    void updateFilmNameBlank() throws Exception {
        Film newFilm = film.toBuilder().id(1).name(null).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)).accept(MediaType.APPLICATION_JSON));
        putToFilmBadRequest(newFilm, "Title can not be empty");
    }

    @Test
    void updateFilmDescriptionMoreThan200Length() throws Exception {
        Film newFilm = film.toBuilder().description(film.getDescription() + film.getDescription()).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)).accept(MediaType.APPLICATION_JSON));
        putToFilmBadRequest(newFilm, "Description should be less than 200 length");
    }

    @Test
    void updateFilmIncorrectDate() throws Exception {
        Film newFilm = film.toBuilder().releaseDate(LocalDate.MIN).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)).accept(MediaType.APPLICATION_JSON));
        putToFilmBadRequest(newFilm, ("Release date less than min release date"));
    }

    @Test
    void updateFilmIncorrectDuration() throws Exception {
        Film newFilm = film.toBuilder().duration(-1).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)).accept(MediaType.APPLICATION_JSON));
        putToFilmBadRequest(newFilm, ("Duration should be greater than 0"));
    }

    @Test
    void updateFilmNotFound() throws Exception {
        Film newFilm = film.toBuilder().id(9999).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)).accept(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFilm))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(f ->
                        Assertions.assertTrue(f.getResponse().getStatus() == HttpServletResponse.SC_NOT_FOUND))
                .andExpect(f ->
                        Assertions.assertTrue(
                                f.getResponse().getContentAsString(Charset.defaultCharset()).contains("Film not found")));

    }

    @Test
    void updateFilm() throws Exception {
        Film newFilm = film.toBuilder().build();
        ResultActions createFilm = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFilm)).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, createFilm.andReturn().getResponse().getStatus());
        Film resultFilm = film.toBuilder()
                .id(1)
                .duration(110)
                .name("Demolution man")
                .description("Stallone and Blade punch each other in the face")
                .releaseDate(LocalDate.of(1993, 8, 12))
                .mpa(mpa1)
                .build();
        ResultActions updateFilm = mockMvc.perform(MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resultFilm))
                .accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, updateFilm.andReturn().getResponse().getStatus());
        ResultActions getAllTask = mockMvc.perform(MockMvcRequestBuilders.get("/films")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(objectMapper.writeValueAsString(new ArrayList<>(Collections.singleton(resultFilm))),
                getAllTask.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getAllTaskHas2Task() throws Exception {
        Film newFilm1 = film.toBuilder().build();
        Film newFilm2 = film.toBuilder()
                .duration(110)
                .name("Demolution man")
                .description("Stallone and Blade punch each other in the face")
                .releaseDate(LocalDate.of(1993, 8, 12))
                .mpa(mpa)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFilm1)).accept(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFilm2)).accept(MediaType.APPLICATION_JSON));
        Film resultFilm1 = newFilm1.toBuilder().id(1).mpa(mpa1).build();
        Film resultFilm2 = newFilm2.toBuilder().id(2).mpa(mpa1).build();
        String resultString = objectMapper.writeValueAsString(List.of(resultFilm1, resultFilm2));
        ResultActions getAllTask = mockMvc.perform(MockMvcRequestBuilders.get("/films")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(resultString, getAllTask.andReturn().getResponse().getContentAsString());
    }

    private void postToFilmBadRequest(Film film, String message) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result ->
                        Assertions.assertTrue(result.getResolvedException()
                                .getMessage()
                                .contains(message)));
    }

    private void putToFilmBadRequest(Film film, String message) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result ->
                        Assertions.assertTrue(result.getResolvedException()
                                .getMessage()
                                .contains(message)));

    }
}