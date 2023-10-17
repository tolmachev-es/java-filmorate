package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    User user = User.builder()
            .login("Evgeny")
            .birthday(LocalDate.of(1999, 5, 22))
            .name("Evgeny")
            .email("example@yandex.ru")
            .friends(new HashSet<>())
            .build();

    @Test
    void createUserIncorrectEmail() throws Exception {
        User newUser = user.toBuilder().email("exampleru").build();
        postToUserBadRequest(newUser, "Email is incorrect");
    }

    @Test
    void createUserIncorrectLogin() throws Exception {
        User newUser = user.toBuilder().login("").build();
        postToUserBadRequest(newUser, "Login must should not be empty");
    }

    @Test
    void createUser() throws Exception {
        User newUser = user.toBuilder().build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .accept(MediaType.APPLICATION_JSON));
        User resultUser = newUser.toBuilder().id(1).build();
        ResultActions getUsers = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(objectMapper.writeValueAsString(new ArrayList<>(Collections.singleton(resultUser))),
                getUsers.andReturn().getResponse().getContentAsString());
    }

    @Test
    void createUserWithoutName() throws Exception {
        User newUser = user.toBuilder().name("").build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .accept(MediaType.APPLICATION_JSON));
        User resultUser = newUser.toBuilder().id(1).name(newUser.getLogin()).build();
        ResultActions getUsers = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(objectMapper.writeValueAsString(new ArrayList<>(Collections.singleton(resultUser))),
                getUsers.andReturn().getResponse().getContentAsString());
    }

    @Test
    void updateUser() throws Exception {
        User newUser = user.toBuilder().build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
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
                .content(objectMapper.writeValueAsString(updateUser))
                .accept(MediaType.APPLICATION_JSON));
        ResultActions getUsers = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(objectMapper.writeValueAsString(new ArrayList<>(Collections.singleton(updateUser))),
                getUsers.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getAllUsersHas2User() throws Exception {
        User newUser = user.toBuilder().name("Ben").login("Ten").build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                .accept(MediaType.APPLICATION_JSON));
        User user1 = user.toBuilder().id(1).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .accept(MediaType.APPLICATION_JSON));
        User user2 = newUser.toBuilder().id(2).build();
        ResultActions getAllUser = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
        Assertions.assertEquals(objectMapper.writeValueAsString(List.of(user1, user2)),
                getAllUser.andReturn().getResponse().getContentAsString());
    }

    private void postToUserBadRequest(User user, String message) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
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