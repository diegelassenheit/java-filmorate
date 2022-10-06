package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private Map<Long, User> users = new HashMap<>();
    int currentMaxId = 0;

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        user = replaceEmptyUserName(user);

        currentMaxId++;
        int id = currentMaxId;

        user.setId(id);

        users.put(user.getId(), user);

        log.info(String.format("Пользователь c user_id=%d создан", user.getId()));
        log.debug(String.format("Значение пользователя = %s", user.toString()));
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException(String.format("Пользователя с id= %d не найдено", user.getId()));
        }
        user = replaceEmptyUserName(user);

        users.put(user.getId(), user);

        log.info(String.format("Пользователь c user_id=%d обновлен", user.getId()));
        log.debug(String.format("Значение пользователя = %s", user.toString()));
        return user;
    }

    @GetMapping("/users")
    public List<User> listAllUsers() {
        return List.copyOf(users.values());
    }
    

    private User replaceEmptyUserName(User user) {
        // можно было бы пересобрать через builder, но только ради избегания проверки на null тут кажется оверхедом
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn(String.format("Поле name пустое. Будет выставлено значение login: %s", user.getLogin()));
            user.setName(user.getLogin());
        }
        return user;
    }
}
