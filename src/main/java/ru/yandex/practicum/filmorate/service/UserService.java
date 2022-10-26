package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        user = replaceEmptyUserName(user);
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        if (userStorage.getUser(user.getId()) == null) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", user.getId()));
        }
        user = replaceEmptyUserName(user);
        user = userStorage.update(user.getId(), user);

        return user;
    }

    public void deleteUser(Long id) {
        checkIfUserExists(id);
        userStorage.delete(id);
    }

    public User getUserById(Long id) {
        checkIfUserExists(id);
        return userStorage.getUser(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        checkIfUserExists(userId);
        checkIfUserExists(friendId);

        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void removeFriend(Long userId, Long friendId) {
        checkIfUserExists(userId);
        checkIfUserExists(friendId);

        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);

        user1.removeFriend(user2.getId());
        user2.removeFriend(user1.getId());
    }

    public List<User> getUserFriends(Long id) {
        return userStorage.getUser(id).getFriends().stream().map(this::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) throws NotFoundException {
        Set<Long> intersection = new HashSet<>(userStorage.getUser(id).getFriends());
        intersection.retainAll(userStorage.getUser(otherId).getFriends());
        return intersection.stream().map(this::getUserById).collect(Collectors.toList());
    }

    private User replaceEmptyUserName(User user) {
        // можно было бы пересобрать через builder, но только ради избегания проверки на null тут кажется оверхедом
        if (user.getName() == null || user.getName().isEmpty()) {
            log.warn(String.format("Поле name пустое. Будет выставлено значение login: %s", user.getLogin()));
            user.setName(user.getLogin());
        }
        return user;
    }

    public void checkIfUserExists(Long userId) {
        /* Можно было бы сделать параметризованную аннотацию для параметра в контроллере вида
           @checkIfModelExists(model=User) Long userId, но не уверен,
           что делать скрытые запросы к БД (потенциально) и держать в объекте аннотации ссылки
           на два стораджа - это хорошая идея. */
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }


}
