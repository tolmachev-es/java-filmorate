package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;
    MockMvc mockMvc;

    User user = User.builder()
            .login("Evgeny")
            .birthday(LocalDate.of(1999, 5, 22))
            .name("Evgeny")
            .email("example@yandex.ru")
            .build();

    @BeforeEach
    void createMock(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createUserIncorrectEmail() {
        User newUser = user.toBuilder().email("exampleru").build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newUser))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));
        try {
            Assertions.assertTrue(nestedServletException.getMessage().contains("Email is incorrect"));
        } catch (NullPointerException e){
            Assertions.assertTrue(e.getMessage().contains("NullPointerException"));
        }
    }

    @Test
    void createUserIncorrectLogin() {
        User newUser = user.toBuilder().login("").build();
        NestedServletException nestedServletException = Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newUser))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest()));
        try {
            Assertions.assertTrue(nestedServletException.getMessage().contains("Login must should not be empty"));
        } catch (NullPointerException e){
            Assertions.assertTrue(e.getMessage().contains("NullPointerException"));
        }
    }

    @Test
    void createUser() throws Exception {
        User newUser = user.toBuilder().build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newUser))
                .accept(MediaType.APPLICATION_JSON));
        User resultUser = newUser.toBuilder().id(1).build();
        ResultActions getUsers = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(convertToJson(resultUser), getUsers.andReturn().getResponse().getContentAsString());
    }

    @Test
    void createUserWithoutName() throws Exception {
        User newUser = user.toBuilder().name("").build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newUser))
                .accept(MediaType.APPLICATION_JSON));
        User resultUser = newUser.toBuilder().id(1).name(newUser.getLogin()).build();
        ResultActions getUsers = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(convertToJson(resultUser), getUsers.andReturn().getResponse().getContentAsString());
    }

    @Test
    void updateUser() throws Exception {
        User newUser = user.toBuilder().build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newUser))
                .accept(MediaType.APPLICATION_JSON));
        User updateUser = user.toBuilder()
                .id(1)
                .name("Jenya")
                .email("example@gmail.com")
                .birthday(LocalDate.EPOCH)
                .login("Wcobq")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateUser))
                .accept(MediaType.APPLICATION_JSON));
        ResultActions getUsers = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(convertToJson(updateUser), getUsers.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getAllUsersHas2User() throws Exception {
        User newUser = user.toBuilder().name("Ben").login("Ten").build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user))
                .accept(MediaType.APPLICATION_JSON));
        User user1 = user.toBuilder().id(1).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newUser))
                .accept(MediaType.APPLICATION_JSON));
        User user2 = newUser.toBuilder().id(2).build();
        String resultString = (convertToJson(user1) + "," + convertToJson(user2))
                .replaceAll("],\\[", ",");
        ResultActions getAllUser = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(resultString, getAllUser.andReturn().getResponse().getContentAsString());
    }

    private static String convertToJson(User user){
        String date = "\"birthday\":\"" + user.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd\""));
        return asJsonString(
                new ArrayList<>(Collections.singleton(user)))
                .replaceAll("\"birthday\":\\[\\d+,\\d+,\\d+]", date);
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