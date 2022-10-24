package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        user = userService.createUser(user);

        log.info(String.format("Пользователь c user_id=%d создан", user.getId()));
        log.debug(String.format("Значение пользователя = %s", user.toString()));

        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        user = userService.updateUser(user);

        log.info(String.format("Пользователь c user_id=%d обновлен", user.getId()));
        log.debug(String.format("Значение пользователя = %s", user.toString()));
        return user;
    }


    @GetMapping(value = "/users/{id}")
    public User getUser(@PathVariable("id") Long id) throws ValidationException {
        User user = userService.getUserById(id);

        log.info(String.format("Запрошен пользователь c user_id=%d", id));
        log.debug(String.format("Значение пользователя = %s", user.toString()));
        return user;
    }

    @GetMapping("/users")
    public List<User> listAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<?> addToFriends(@PathVariable("id") Long id,
                                          @PathVariable("friendId") Long friendId) {
        userService.addFriend(id, friendId);
        log.info(String.format("Пользователи %d и %d добавлены в друзья друг другу", id, friendId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFromFiends(@PathVariable("id") Long id,
                                              @PathVariable("friendId") Long friendId) {
        userService.removeFriend(id, friendId);
        log.info(String.format("Пользователи %d и %d удалены из друзей друг друга", id, friendId));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("/users/{id}/friends")
    public List<User> listFriends(@PathVariable("id") Long id) {
        List<User> users = userService.getUserFriends(id);
        log.info(String.format("Получен список друзей %d", id));
        log.debug(String.format("Друзья пользователя = %s", users.toString()));
        return users;
    }


    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> listCommonFriends(@PathVariable("id") Long id, @PathVariable("otherId") Long otherId) {
        List<User> users = userService.getCommonFriends(id, otherId);
        log.info(String.format("Получен список общих друзей %d и %d", id, otherId));
        log.debug(String.format("Друзья пользователя = %s", users.toString()));
        return users;
    }
}
