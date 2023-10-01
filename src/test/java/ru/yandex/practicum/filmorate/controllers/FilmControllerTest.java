package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;
    MockMvc mockMvc;
    Film film = Film.builder()
            .id(1)
            .title("The Man From Earth")
            .releaseDate(LocalDate.of(2007, 2, 14))
            .description("It stars David Lee Smith as John Oldman, a " +
                    "departing university professor, who puts forth the notion that he is more than 14,000 years old.")
            .duration(87)
            .build();

    @BeforeEach
    void createController(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createFilmNameBlank(){
        Film newFilm = film.toBuilder().title("").build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));

        Assertions.assertTrue(nestedServletException.getMessage().contains("Title can not be empty"));
    }
    @Test
    void createFilmDescriptionMoreThan200Length(){
        Film newFilm = film.toBuilder().description(film.getDescription() + film.getDescription()).build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));
        Assertions.assertTrue(nestedServletException.getMessage().contains("Description should be less than 200 length"));
    }

    @Test
    void createFilmIncorrectDate(){
        Film newFilm = film.toBuilder().releaseDate(LocalDate.MIN).build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));
        Assertions.assertTrue(nestedServletException.getMessage().contains("Release date less than min release date"));
    }

    @Test
    void createFilmIncorrectDuration(){
        Film newFilm = film.toBuilder().duration(-1).build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));
        Assertions.assertTrue(nestedServletException.getMessage().contains("Duration should be greater than 0"));
    }

    @Test
    void createNewFilm() throws Exception {
        Film newFilm = film.toBuilder().build();
        ResultActions ra = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newFilm)).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, ra.andReturn().getResponse().getStatus());
        Film resultFilm = film.toBuilder().id(1).build();
        ResultActions getAllTask = mockMvc.perform(MockMvcRequestBuilders.get("/films")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(convertToJson(resultFilm), getAllTask.andReturn().getResponse().getContentAsString());
    }

    @Test
    void updateFilmNameBlank(){
        Film newFilm = film.toBuilder().title("").build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest()));
        Assertions.assertTrue(nestedServletException.getMessage().contains("Name can not be empty"));
    }

    @Test
    void updateFilmDescriptionMoreThan200Length(){
        Film newFilm = film.toBuilder().description(film.getDescription() + film.getDescription()).build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest()));
        Assertions.assertTrue(nestedServletException.getMessage().contains("Description should be less than 200 length"));
    }

    @Test
    void updateFilmIncorrectDate(){
        Film newFilm = film.toBuilder().releaseDate(LocalDate.MIN).build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));
        Assertions.assertTrue(nestedServletException.getMessage().contains("Release date less than min release date"));
    }

    @Test
    void updateFilmIncorrectDuration(){
        Film newFilm = film.toBuilder().duration(-1).build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));
        try {
            Assertions.assertTrue(nestedServletException.getMessage().contains("Duration should be greater than 0"));
        } catch (NullPointerException e){
            Assertions.fail();
        }
    }

    @Test
    void updateFilmNotFound(){
        Film newFilm = film.toBuilder().id(9999).build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newFilm))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));
        try {
            Assertions.assertTrue(nestedServletException.getMessage().contains("Film not found"));
        } catch (NullPointerException e){
            Assertions.fail();
        }
    }

    @Test
    void updateFilm() throws Exception {
        Film newFilm = film.toBuilder().build();
        ResultActions createFilm = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newFilm)).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, createFilm.andReturn().getResponse().getStatus());
        Film resultFilm = film.toBuilder()
                .id(1)
                .duration(110)
                .title("Demolution man")
                .description("Stallone and Blade punch each other in the face")
                .releaseDate(LocalDate.of(1993, 8, 12))
                .build();
        ResultActions updateFilm = mockMvc.perform(MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(resultFilm))
                .accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(200, updateFilm.andReturn().getResponse().getStatus());
        ResultActions getAllTask = mockMvc.perform(MockMvcRequestBuilders.get("/films")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(convertToJson(resultFilm), getAllTask.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getAllTaskHas2Task() throws Exception {
        Film newFilm1 = film.toBuilder().build();
        Film newFilm2 = film.toBuilder()
                .duration(110)
                .title("Demolution man")
                .description("Stallone and Blade punch each other in the face")
                .releaseDate(LocalDate.of(1993, 8, 12))
                .build();
        ResultActions createFilm1 = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newFilm1)).accept(MediaType.APPLICATION_JSON));
        ResultActions createFilm2 = mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newFilm2)).accept(MediaType.APPLICATION_JSON));
        Film resultFilm1 = newFilm1.toBuilder().id(1).build();
        Film resultFilm2 = newFilm2.toBuilder().id(2).build();
        String resultString = (convertToJson(resultFilm1) + "," + convertToJson(resultFilm2))
                .replaceAll("],\\[", ",");
        ResultActions getAllTask = mockMvc.perform(MockMvcRequestBuilders.get("/films")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(resultString, getAllTask.andReturn().getResponse().getContentAsString());
    }

    private static String convertToJson(Film film){
        String date = "\"releaseDate\":\"" + film.getReleaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd\""));
        return asJsonString(
                new ArrayList<>(Collections.singleton(film)))
                .replaceAll("\"releaseDate\":\\[\\d+,\\d+,\\d+]", date);
    }

    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(obj);
        } catch (RuntimeException | JsonProcessingException e){
            throw new RuntimeException();
        }
    }
}