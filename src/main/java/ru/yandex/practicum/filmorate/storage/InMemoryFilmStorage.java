package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    public Map<Long, Film> films = new HashMap<>();
    Long currentMaxId = 0L;

    @Override
    public void create(Film film) {
        currentMaxId++;
        Long id = currentMaxId;
        film.setId(id);
        films.put(film.getId(), film);
    }

    @Override
    public void update(Long id, Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void delete(Long id) {
        films.remove(id);
    }

    @Override
    public Film get(Long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }
}
