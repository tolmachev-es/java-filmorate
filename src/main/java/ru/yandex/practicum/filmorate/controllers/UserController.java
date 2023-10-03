package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private int id = 1;
    private final Map<Integer, User> userMap = new HashMap<>();

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        log.trace("Start create user");
        User newUser = user.toBuilder()
                .id(getNextId())
                .name((user.getName() == null || user.getName().isBlank()) ? user.getLogin() : user.getName())
                .build();
        userMap.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (userMap.containsKey(user.getId())) {
            User newUser = userMap.get(user.getId()).toBuilder()
                    .email(user.getEmail())
                    .login(user.getLogin())
                    .name(user.getName().isEmpty() ? user.getLogin() : user.getName())
                    .birthday(user.getBirthday())
                    .build();
            userMap.put(newUser.getId(), newUser);
            return newUser;
        } else {
            throw new ValidationException("User not found");
        }
    }

    @GetMapping("/users")
    public List<User> getAllUser() {
        return new ArrayList<>(userMap.values());
    }

    private int getNextId() {
        return id++;
    }
}
