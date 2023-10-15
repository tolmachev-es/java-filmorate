package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Start create user");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Start update user");
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUser() {
        log.info("Start get all user");
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Start getting user by id");
        return userService.getUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Start add friend");
        return userService.addNewFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Start remove friend");
        return userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUser(@PathVariable Integer id) {
        log.info("Start get friend by userId");
        return userService.getFriendsList(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getMutualFriend(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Start get mutual friend");
        return userService.getMutualFriendsList(id, otherId);
    }
}
