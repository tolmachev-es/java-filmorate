package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Qualifier(value = "UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(Integer id) {
        SqlRowSet userRows = jdbcTemplate
                .queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", id);
        if (userRows.next()) {
            return convertToObject(userRows);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("UPDATE USERS " +
                        "SET NAME = ?, " +
                        "EMAIL = ?, " +
                        "LOGIN = ?, " +
                        "BIRTHDAY = ? " +
                        "WHERE USER_ID = ?",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return getUser(user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");
        List<User> users = new ArrayList<>();
        while (userRows.next()) {
            users.add(convertToObject(userRows));
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        jdbcTemplate.update("INSERT INTO USERS (NAME, EMAIL, LOGIN, BIRTHDAY) VALUES (?, ?, ?, ?)",
                user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());
        SqlRowSet userRow = jdbcTemplate.queryForRowSet("select max(user_id) as max_user_id from users");
        userRow.next();
        return getUser(Integer.parseInt(Objects.requireNonNull(userRow.getString("max_user_id"))));
    }

    @Override
    public User removeFriend(int userId1, int userId2) {
        getUser(userId1);
        getUser(userId2);
        SqlRowSet friendRequest = getFriendRequest(userId1, userId2);
        if (friendRequest.next()) {
            jdbcTemplate.update("DELETE FROM FRIEND_REQUEST WHERE FRIEND_REQUEST_ID = ?",
                    friendRequest.getString("FRIEND_REQUEST_ID"));
            return getUser(userId1);
        } else {
            throw new NotFoundException("Not found friend request");
        }
    }

    @Override
    public List<User> getMutualFriendsList(int userId1, int userId2) {
        Set<User> allFriendsFirstUser = new HashSet<>(getFriendsList(userId1));
        Set<User> allFriendsSecondUser = new HashSet<>(getFriendsList(userId2));
        if (allFriendsFirstUser == null) {
            return new ArrayList<>();
        } else {
            allFriendsFirstUser.retainAll(allFriendsSecondUser);
            return new ArrayList<>(allFriendsFirstUser);
        }
    }

    @Override
    public User addNewFriend(int userId1, int userId2) {
        getUser(userId1);
        getUser(userId2);
        SqlRowSet friendSet = getFriendRequest(userId1, userId2);
        if (friendSet.next()) {
            if (friendSet.getBoolean("IS_ALLOWED")) {
                throw new AlreadyExistException("this users already friend");
            } else if (Objects.equals(friendSet.getString("FROM_USER"), String.valueOf(userId1))) {
                throw new AlreadyExistException("request to friendship already exist");
            } else {
                jdbcTemplate.update("UPDATE FRIEND_REQUEST \n" +
                        "SET IS_ALLOWED = TRUE \n" +
                        "WHERE FRIEND_REQUEST_ID = ?", friendSet.getString("FRIEND_REQUEST_ID"));
            }
        } else {
            jdbcTemplate.update("INSERT INTO FRIEND_REQUEST (FROM_USER, TO_USER, IS_ALLOWED)" +
                    "VALUES (?, ?, false)", userId1, userId2);
        }
        return getUser(userId2);
    }

    @Override
    public List<User> getFriendsList(int userId) {
        getUser(userId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT TO_USER FROM FRIEND_REQUEST fr \n" +
                "WHERE FROM_USER = ?\n" +
                "UNION \n" +
                "SELECT FROM_USER FROM FRIEND_REQUEST fr2 \n" +
                "WHERE TO_USER = ?\n" +
                "AND IS_ALLOWED = TRUE", userId, userId);
        List<User> users = new ArrayList<>();
        while (userRows.next()) {
            users.add(getUser(Integer.valueOf(Objects.requireNonNull(userRows.getString("TO_USER")))));
        }
        return users;
    }

    private SqlRowSet getFriendRequest(int userId1, int userId2) {
        return jdbcTemplate.queryForRowSet("SELECT * FROM FRIEND_REQUEST fr " +
                "WHERE FROM_USER IN (?, ?)" +
                "AND TO_USER IN (?, ?)", userId1, userId2, userId1, userId2);
    }

    private User convertToObject(SqlRowSet userRow) {
        return User.builder()
                .id(Integer.parseInt(Objects.requireNonNull(userRow.getString("user_id"))))
                .name(userRow.getString("name"))
                .email(userRow.getString("email"))
                .login(userRow.getString("login"))
                .birthday(LocalDate.parse(Objects.requireNonNull(userRow.getString("birthday"))))
                .build();
    }
}
