package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    int id;
    @Email
    @NotEmpty
    String email;
    @NotNull
    @NotEmpty
    String login;
    String name;
    @NotNull
    @Past
    LocalDate birthday;
}
