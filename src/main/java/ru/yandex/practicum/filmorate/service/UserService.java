package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        try {
            User oldUser = inMemoryUserStorage.getUser(user.getId());
            return inMemoryUserStorage.updateUser(user.toBuilder()
                    .friends(oldUser.getFriends())
                    .build());
        } catch (RuntimeException e) {
            throw new NotFoundException("Пользователь не найден");
        }
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
        inMemoryUserStorage.getUser(userId1).toBuilder()
                .friends(getAdditionUserList(userId1, userId2))
                .build();
        inMemoryUserStorage.getUser(userId2).toBuilder()
                .friends(getAdditionUserList(userId2, userId1))
                .build();
        return inMemoryUserStorage.getUser(userId1);
    }

    public User removeFriend(int userId1, int userId2) {
        inMemoryUserStorage.getUser(userId1).toBuilder()
                .friends(getRemoveUserList(userId1, userId2))
                .build();
        inMemoryUserStorage.getUser(userId2).toBuilder()
                .friends(getRemoveUserList(userId2, userId1))
                .build();
        return inMemoryUserStorage.getUser(userId1);
    }

    private Set<Integer> getAdditionUserList(int inUser, int fromUser) {
        Set<Integer> newUserList = inMemoryUserStorage.getUser(inUser).getFriends();
        Set<Integer> secondUserList = inMemoryUserStorage.getUser(fromUser).getFriends();
        secondUserList.add(inUser);
        newUserList.add(fromUser);
        return newUserList;
    }

    private Set<Integer> getRemoveUserList(int fromUser, int userToRemove) {
        Set<Integer> withoutUserToRemove = inMemoryUserStorage.getUser(fromUser).getFriends();
        withoutUserToRemove.remove(inMemoryUserStorage.getUser(userToRemove).getId());
        //сделано так, что бы дернуть проверку что пользователь есть в списке
        return withoutUserToRemove;
    }


    private int getNextId() {
        return userId++;
    }
}
