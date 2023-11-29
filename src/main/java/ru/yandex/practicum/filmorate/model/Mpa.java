package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@Value
public class Mpa {
    @NotNull(message = "Only number")
    int id;
    String name;
}