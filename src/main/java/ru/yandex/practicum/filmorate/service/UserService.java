package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    @NotNull
    private final UserStorage userStorage;

    public List<User> getAllUser() {
        return userStorage.getAllUsers();
    }

    public User getUser(Integer userId) {
        return userStorage.getUser(userId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user.toBuilder()
                .name((user.getName() == null || user.getName().isBlank()) ? user.getLogin() : user.getName())
                .friends(new HashSet<>())
                .build());
    }

    public User updateUser(User user) {
        User oldUser = userStorage.getUser(user.getId());
        return userStorage.updateUser(user.toBuilder()
                .friends(oldUser.getFriends())
                .build());
    }

    public List<User> getFriendsList(int userId) {
        return userStorage.getFriendsList(userId);
    }

    public List<User> getMutualFriendsList(int userId1, int userId2) {
        return userStorage.getMutualFriendsList(userId1, userId2);
    }

    public User addNewFriend(int userId1, int userId2) {
        return userStorage.addNewFriend(userId1, userId2);
    }

    public User removeFriend(int userId1, int userId2) {
        return userStorage.removeFriend(userId1, userId2);
    }
}
