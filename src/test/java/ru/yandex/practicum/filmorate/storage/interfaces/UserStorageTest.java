package ru.yandex.practicum.filmorate.storage.interfaces;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserStorageTest {

    private final UserDbStorage userDbStorage;

    User user = User.builder()
            .login("Evgeny")
            .birthday(LocalDate.of(1999, 5, 22))
            .name("Evgeny")
            .email("example@yandex.ru")
            .build();
    User user1 = User.builder()
            .login("Evgeny1")
            .birthday(LocalDate.of(1999, 5, 20))
            .name("Evgeny1")
            .email("example1@yandex.ru")
            .build();

    User user2 = User.builder()
            .login("Evgeny2")
            .birthday(LocalDate.of(1999, 5, 2))
            .email("example2@yandex.ru")
            .build();

    @Test
    void createUser() {
        userDbStorage.createUser(user);
        User newUser = user.toBuilder().id(1).build();
        User getUser = userDbStorage.getUser(1);
        Assertions.assertEquals(newUser, getUser);
    }

    @Test
    void updateUser() {
        userDbStorage.createUser(user);
        User userUpdate = user1.toBuilder().id(1).build();
        userDbStorage.updateUser(userUpdate);
        User getUser = userDbStorage.getUser(1);
        Assertions.assertEquals(userUpdate, getUser);
    }

    @Test
    void getAllUser() {
        userDbStorage.createUser(user);
        userDbStorage.createUser(user1);
        User userAfterId = user.toBuilder().id(1).build();
        User userAfterId1 = user1.toBuilder().id(2).build();
        List<User> users = userDbStorage.getAllUsers();
        Assertions.assertEquals(List.of(userAfterId, userAfterId1), users);
    }

    @Test
    void sendFriendRequestAndRemove() {
        userDbStorage.createUser(user);
        userDbStorage.createUser(user1);
        User userAfterId = user.toBuilder().id(1).build();
        User userAfterId1 = user1.toBuilder().id(2).build();
        userDbStorage.addNewFriend(userAfterId.getId(), userAfterId1.getId());
        Assertions.assertEquals(List.of(userAfterId1), userDbStorage.getFriendsList(userAfterId.getId()));
        userDbStorage.addNewFriend(userAfterId1.getId(), userAfterId.getId());
        Assertions.assertEquals(List.of(userAfterId), userDbStorage.getFriendsList(userAfterId1.getId()));
        userDbStorage.removeFriend(userAfterId.getId(), userAfterId1.getId());
        Assertions.assertEquals(new ArrayList<>(), userDbStorage.getFriendsList(userAfterId.getId()));
        Assertions.assertEquals(new ArrayList<>(), userDbStorage.getFriendsList(userAfterId1.getId()));
    }

    @Test
    void getMutualFriends() {
        userDbStorage.createUser(user);
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);
        User userAfterId = user.toBuilder().id(1).build();
        User userAfterId1 = user1.toBuilder().id(2).build();
        User userAfterId2 = user2.toBuilder().id(3).build();
        userDbStorage.addNewFriend(userAfterId.getId(), userAfterId1.getId());
        Assertions.assertEquals(List.of(userAfterId1), userDbStorage.getFriendsList(userAfterId.getId()));
        userDbStorage.addNewFriend(userAfterId2.getId(), userAfterId1.getId());
        Assertions.assertEquals(List.of(userAfterId1), userDbStorage.getFriendsList(userAfterId.getId()));
        List<User> mutualFriend = userDbStorage.getMutualFriendsList(userAfterId.getId(), userAfterId2.getId());
        Assertions.assertEquals(List.of(userAfterId1), mutualFriend);
    }

    @Test
    void getUnknownUser() {
        NotFoundException notFoundException = Assertions.assertThrows(
                NotFoundException.class, () -> userDbStorage.getUser(1));
        Assertions.assertEquals("User not found", notFoundException.getMessage());
    }

    @Test
    void addFriendExceptions() {
        userDbStorage.createUser(user);
        userDbStorage.createUser(user1);
        User userAfterId = user.toBuilder().id(1).build();
        User userAfterId1 = user1.toBuilder().id(2).build();
        userDbStorage.addNewFriend(userAfterId.getId(), userAfterId1.getId());
        AlreadyExistException alreadyExistException = Assertions.assertThrows(
                AlreadyExistException.class,
                () -> userDbStorage.addNewFriend(userAfterId.getId(), userAfterId1.getId()));
        Assertions.assertEquals("request to friendship already exist", alreadyExistException.getMessage());
        userDbStorage.addNewFriend(userAfterId1.getId(), userAfterId.getId());
        AlreadyExistException alreadyExistException1 = Assertions.assertThrows(
                AlreadyExistException.class,
                () -> userDbStorage.addNewFriend(userAfterId.getId(), userAfterId1.getId()));
        Assertions.assertEquals("this users already friend", alreadyExistException1.getMessage());
    }
}