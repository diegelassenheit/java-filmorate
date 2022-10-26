package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public void create(Film film);

    public void update(Long id, Film film);

    public void delete(Long id);

    public Film get(Long id);

    public List<Film> getAll();
}
