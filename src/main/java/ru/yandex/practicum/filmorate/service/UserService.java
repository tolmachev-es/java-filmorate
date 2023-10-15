package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;
    @Value(value = "1")
    private int userId;

    public List<User> getAllUser() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User getUser(int userId) {
        return inMemoryUserStorage.getUser(userId);
    }

    public User createUser(User user) {
        User newUser = user.toBuilder()
                .name((user.getName() == null || user.getName().isBlank()) ? user.getLogin() : user.getName())
                .friends(new HashSet<>())
                .id(getNextId()).build();
        return inMemoryUserStorage.updateUser(newUser);
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
        return new ArrayList<>(inMemoryUserStorage.getUser(userId).getFriends());
    }

    public List<User> getMutualFriendsList(int userId1, int userId2) {
        Set<User> allFriendsFirstUser = new HashSet<>(inMemoryUserStorage.getUser(userId1).getFriends());
        Set<User> allFriendsSecondUser = new HashSet<>(inMemoryUserStorage.getUser(userId2).getFriends());
        if (allFriendsSecondUser == null) {
            return new ArrayList<>();
        } else {
            allFriendsFirstUser.retainAll(allFriendsSecondUser);
            return new ArrayList<>(allFriendsFirstUser);
        }

    }

    public User addNewFriend(int userId1, int userId2) {
        User updatingUser1 = inMemoryUserStorage.getUser(userId1).toBuilder()
                .friends(getAdditionUserList(userId1, userId2))
                .build();
        User updatingUser2 = inMemoryUserStorage.getUser(userId2).toBuilder()
                .friends(getAdditionUserList(userId2, userId1))
                .build();
        inMemoryUserStorage.updateUser(updatingUser1);
        inMemoryUserStorage.updateUser(updatingUser2);
        return updatingUser1;
    }

    public User removeFriend(int userId1, int userId2) {
        User updatingUser1 = inMemoryUserStorage.getUser(userId1).toBuilder()
                .friends(getRemoveUserList(userId1, userId2))
                .build();
        inMemoryUserStorage.updateUser(updatingUser1);
        User updatingUser2 = inMemoryUserStorage.getUser(userId2).toBuilder()
                .friends(getRemoveUserList(userId2, userId1))
                .build();
        inMemoryUserStorage.updateUser(updatingUser2);
        return updatingUser1;
    }

    private Set<User> getAdditionUserList(int inUser, int fromUser) {
        Set<User> newUserList = inMemoryUserStorage.getUser(inUser).getFriends();
        User secondUser = inMemoryUserStorage.getUser(fromUser);
        Set<User> secondUserList = secondUser.getFriends();
        secondUserList.add(inMemoryUserStorage.getUser(inUser));
        newUserList.add(secondUser.toBuilder().friends(secondUserList).build());
        return newUserList;
    }

    private Set<User> getRemoveUserList(int fromUser, int userToRemove) {
        Set<User> withoutUserToRemove = inMemoryUserStorage.getUser(fromUser).getFriends();
        withoutUserToRemove.remove(inMemoryUserStorage.getUser(userToRemove));
        return withoutUserToRemove;
    }


    private int getNextId() {
        return userId++;
    }
}
