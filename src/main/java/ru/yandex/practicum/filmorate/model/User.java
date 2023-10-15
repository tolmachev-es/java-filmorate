package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class User {
    int id;
    @Email(message = "Email is incorrect")
    String email;
    @NotBlank(message = "Login must should not be empty")
    String login;
    @Builder.Default
    String name = "";
    @Past(message = "Birthday must should be less than today")
    LocalDate birthday;
    @JsonBackReference
    Set<User> friends;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email) && Objects.equals(login, user.login) && Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday);
    }
}
