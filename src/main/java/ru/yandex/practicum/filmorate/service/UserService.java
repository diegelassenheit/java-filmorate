package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    Long currentMaxId = 0L; // не был уверен, переносить id в стораджи или нет. Оставил тут
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        user = replaceEmptyUserName(user);
        currentMaxId++;
        Long id = currentMaxId;
        user.setId(id);

        return userStorage.create(id, user);
    }

    public User updateUser(User user) throws ValidationException {
        userStorage.checkIfUserExists(user.getId());

        user = replaceEmptyUserName(user);
        user = userStorage.update(user.getId(), user);

        return user;
    }

    public void deleteUser(Long id) throws NotFoundException {
        userStorage.checkIfUserExists(id);
        userStorage.delete(id);
    }

    public User getUserById(Long id) throws NotFoundException {
        userStorage.checkIfUserExists(id);
        return userStorage.getUser(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        userStorage.checkIfUserExists(userId);
        userStorage.checkIfUserExists(friendId);

        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void removeFriend(Long userId, Long friendId) throws NotFoundException {
        userStorage.checkIfUserExists(userId);
        userStorage.checkIfUserExists(friendId);

        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);

        user1.removeFriend(user2.getId());
        user2.removeFriend(user1.getId());
    }

    public List<User> getUserFriends(Long id) throws NotFoundException {
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


}
