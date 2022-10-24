package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User create(Long id, User user);
    public User update(Long id, User user);
    public void delete(Long id);
    public User getUser(Long id);
    public List<User> getAllUsers();
    public boolean contains(Long key);
    public void checkIfUserExists(Long userId) throws NotFoundException;

}
