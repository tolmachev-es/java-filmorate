package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {
    private int id = 1;
    private Map<Integer, User> userMap = new HashMap<>();

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user, BindingResult bindingResult){
        log.trace("Start create user");
        validateUser(user, bindingResult);
        User newUser = user.toBuilder()
                .id(getNextId())
                .name(user.getName().isBlank() ? user.getLogin() : user.getName())
                .build();
        userMap.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult){
        validateUser(user, bindingResult);
        if(userMap.containsKey(user.getId())){
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
    public List<User> getAllUser(){
        return new ArrayList<>(userMap.values());
    }

    private void validateUser(User user, BindingResult bindingResult){
        log.info("Start validating user");
        StringBuilder errorMsg = new StringBuilder(bindingResult.getFieldErrors()
                .stream()
                .map(s -> s.getField() + " " + s.getDefaultMessage())
                .collect(Collectors.joining(";")));
        if(errorMsg.length() > 0){
            throw new ValidationException(errorMsg.toString());
        }
        log.info("User is valid");
    }

    private int getNextId(){
        return id++;
    }
}
