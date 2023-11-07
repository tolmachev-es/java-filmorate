package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();


    @Override
    public User getUser(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new AlreadyExistException("User already exist in storage");
        } else {
            users.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public List<User> getFriendsList(int userId) {
        return getUser(userId)
                .getFriends()
                .stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public User addNewFriend(int userId1, int userId2) {
        getUser(userId1).getFriends().add(getUser(userId2).getId());
        getUser(userId2).getFriends().add(getUser(userId1).getId());
        return getUser(userId2);
    }

    @Override
    public User removeFriend(int userId1, int userId2) {
        getUser(userId1).getFriends().remove(getUser(userId2).getId());
        getUser(userId2).getFriends().remove(getUser(userId1).getId());
        return getUser(userId1);
    }

    @Override
    public List<User> getMutualFriendsList(int userId1, int userId2) {
        Set<Integer> allFriendsFirstUser = getFriendsList(userId1).stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        Set<Integer> allFriendsSecondUser = getFriendsList(userId2)
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        if (allFriendsSecondUser == null) {
            return new ArrayList<>();
        } else {
            allFriendsFirstUser.retainAll(allFriendsSecondUser);
            return allFriendsFirstUser.stream()
                    .map(this::getUser)
                    .collect(Collectors.toList());
        }
    }
}
