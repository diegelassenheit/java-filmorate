package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    public void create(Long id, Film film);
    public void update(Long id, Film film);
    public void delete(Long id);
    public Film get(Long id);
    public List<Film> getAll();
    public boolean contains(Long key);

}
