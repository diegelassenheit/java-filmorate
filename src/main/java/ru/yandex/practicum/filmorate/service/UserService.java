package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
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
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);

        userStorage.addFriend(user1.getId(), user2.getId());
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long id) {
        return userStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) throws NotFoundException {
        List<User> usersFriends = userStorage.getAllFriends(id);
        List<User> otherUsersFriends = userStorage.getAllFriends(otherId);

        List<User> result = usersFriends.stream()
                .distinct()
                .filter(otherUsersFriends::contains).collect(Collectors.toList());

        return result;
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
