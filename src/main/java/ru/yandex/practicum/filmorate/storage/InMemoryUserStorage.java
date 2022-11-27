package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long currentMaxId = 0L;

    @Override
    public User create(User user) {
        currentMaxId++;
        Long id = currentMaxId;
        user.setId(id);

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public boolean contains(Long key) {
        return users.containsKey(key);
    }

    @Override
    public void addFriend(long userId, long friendId) {

    }

    @Override
    public void removeFriend(long userId, long friendId) {
    }

    @Override
    public List<User> getAllFriends(long userId) {
        return null;
    }

    @Override
    public List<User> getMutalFriends(long userId) {

        return null;
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        return null;
    }

    @Override
    public User update(Long id, User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
