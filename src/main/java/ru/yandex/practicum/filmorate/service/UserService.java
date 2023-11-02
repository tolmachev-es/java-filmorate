package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;
    private int userId = 1;

    public List<User> getAllUser() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User getUser(int userId) {
        return inMemoryUserStorage.getUser(userId);
    }

    public User createUser(User user) {
        return inMemoryUserStorage.createUser(user.toBuilder()
                .name((user.getName() == null || user.getName().isBlank()) ? user.getLogin() : user.getName())
                .friends(new HashSet<>())
                .id(getNextId()).build());
    }

    public User updateUser(User user) {
        User oldUser = inMemoryUserStorage.getUser(user.getId());
        return inMemoryUserStorage.updateUser(user.toBuilder()
                .friends(oldUser.getFriends())
                .build());
    }

    public List<User> getFriendsList(int userId) {
        return inMemoryUserStorage.getUser(userId)
                .getFriends()
                .stream()
                .map(inMemoryUserStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriendsList(int userId1, int userId2) {
        Set<Integer> allFriendsFirstUser = new HashSet<>(inMemoryUserStorage.getUser(userId1).getFriends());
        Set<Integer> allFriendsSecondUser = new HashSet<>(inMemoryUserStorage.getUser(userId2).getFriends());
        if (allFriendsSecondUser == null) {
            return new ArrayList<>();
        } else {
            allFriendsFirstUser.retainAll(allFriendsSecondUser);
            return allFriendsFirstUser.stream().map(inMemoryUserStorage::getUser).collect(Collectors.toList());
        }

    }

    public User addNewFriend(int userId1, int userId2) {
        inMemoryUserStorage.getUser(userId1).getFriends().add(inMemoryUserStorage.getUser(userId2).getId());
        inMemoryUserStorage.getUser(userId2).getFriends().add(inMemoryUserStorage.getUser(userId1).getId());
        return inMemoryUserStorage.getUser(userId1);
    }

    public User removeFriend(int userId1, int userId2) {
        inMemoryUserStorage.getUser(userId1).getFriends().remove(inMemoryUserStorage.getUser(userId2).getId());
        inMemoryUserStorage.getUser(userId2).getFriends().remove(inMemoryUserStorage.getUser(userId1).getId());
        return inMemoryUserStorage.getUser(userId1);
    }

    private int getNextId() {
        return userId++;
    }
}
