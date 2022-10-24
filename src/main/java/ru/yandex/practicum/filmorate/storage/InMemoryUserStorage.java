package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(Long id, User user) {
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
    public User update(Long id, User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    public void checkIfUserExists(Long userId) throws NotFoundException {
        /* Можно было бы сделать параметризованную аннотацию для параметра в контроллере вида
           @checkIfModelExists(model=User) Long userId, но не уверен,
           что делать скрытые запросы к БД (потенциально) и держать в объекте аннотации ссылки
           на два стораджа - это хорошая идея. */
        if (!contains(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }
}
