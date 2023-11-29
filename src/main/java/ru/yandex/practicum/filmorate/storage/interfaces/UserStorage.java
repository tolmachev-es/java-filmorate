package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User getUser(Integer id);

    User updateUser(User user);

    List<User> getAllUsers();

    User createUser(User user);

    User removeFriend(int userId1, int userId2);

    List<User> getMutualFriendsList(int userId1, int userId2);

    List<User> getFriendsList(int userId);

    User addNewFriend(int userId1, int userId2);
}
