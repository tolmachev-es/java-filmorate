package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User getUser(Integer id);

    User updateUser(User user);

    List<User> getAllUsers();
}
