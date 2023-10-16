package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
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
    Set<Integer> friends;
}
