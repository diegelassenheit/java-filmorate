package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User create(User user);
    public User update(Long id, User user);
    public void delete(Long id);
    public User getUser(Long id);
    public List<User> getAllUsers();
    public boolean contains(Long key);

}
