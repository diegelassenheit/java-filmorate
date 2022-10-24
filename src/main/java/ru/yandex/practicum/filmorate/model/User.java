package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=false)
public class User extends FilmorateModel {
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
    private Set<Long> friends = new HashSet<>();

    public void addFriend(Long friendId) {
       friends.add(friendId);
    }

    public void removeFriend(Long friendId) {
       friends.remove(friendId);
    }
}
