package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    long id;
    @Email
    @NotEmpty
    private String email;
    @NotNull
    @NotEmpty
    private String login;
    private String name;
    @NotNull
    @Past
    private LocalDate birthday;
}
