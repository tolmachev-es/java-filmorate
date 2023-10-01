package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class Film {
    int id;
    @NotBlank(message = "Title can not be empty")
    String name;
    @Length(max = 200, message = "Description should be less than 200 length")
    String description;
    @NotNull(message = "Release date is incorrect")
    LocalDate releaseDate;
    @Positive(message = "Duration should be greater than 0")
    int duration;
    public static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
}
