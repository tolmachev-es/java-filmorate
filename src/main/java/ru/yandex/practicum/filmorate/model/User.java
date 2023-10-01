package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class User {
    @Min(value = 0, message = "Id should be greater than 0")
    int id;
    @Email(message = "Email is incorrect")
    String email;
    @NotBlank(message = "Login must should not be empty")
    String login;
    @Builder.Default
    String name = "";
    @Past(message = "Birthday must should be less than today")
    LocalDate birthday;
}
