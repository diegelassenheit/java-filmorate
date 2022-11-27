package ru.yandex.practicum.filmorate.model;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
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
