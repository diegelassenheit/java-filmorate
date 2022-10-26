package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.util.IsAfter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@EqualsAndHashCode(callSuper = false)
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
    private Set<Long> likers = new HashSet<>();

    public void addLikeFromUser(Long userId) {
        likers.add(userId);
    }

    public void removeLikeFromUser(Long userId) {
        likers.remove(userId);
    }

    public int getNumberOfLikes() {
        return likers.size();
    }
}

