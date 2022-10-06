package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.util.IsAfter;

import javax.validation.Constraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Data
public class Film {
    private long id;
    @NotNull
    @NotEmpty(message = "Поле имя не может быть пустым")
    private String name;
    @Size(min = 1, message = "Описание фильма должно содержать хотя бы один символ")
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;
    @IsAfter(current = "1895-12-28", message = "Поле даты релиза не должно быть ранее 28.12.1895")
    private LocalDate releaseDate;
    @Positive(message = "Поле продолжительности фильма не может быть меньше 0")
    private int duration;
}

