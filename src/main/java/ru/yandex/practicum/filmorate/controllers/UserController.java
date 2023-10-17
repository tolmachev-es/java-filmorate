package ru.yandex.practicum.filmorate.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "500", description = "Server error")
})
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создание пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "User not valid")
    })
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Start create user");
        return userService.createUser(user);
    }

    @Operation(summary = "Обновление пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not valid")
    })
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Start update user");
        return userService.updateUser(user);
    }

    @Operation(summary = "Получение списка всех пользователей")
    @GetMapping
    public List<User> getAllUser() {
        log.info("Start get all user");
        return userService.getAllUser();
    }

    @Operation(summary = "Получение пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Start getting user by id");
        return userService.getUser(id);
    }

    @Operation(summary = "Добавление пользователя в друзья")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Start add friend");
        return userService.addNewFriend(id, friendId);
    }

    @Operation(summary = "Удаление пользователя из друзей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Start remove friend");
        return userService.removeFriend(id, friendId);
    }

    @Operation(summary = "Получение друзей выбранного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUser(@PathVariable Integer id) {
        log.info("Start get friend by userId");
        return userService.getFriendsList(id);
    }

    @Operation(summary = "Показать список общих друзей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getMutualFriend(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Start get mutual friend");
        return userService.getMutualFriendsList(id, otherId);
    }
}
