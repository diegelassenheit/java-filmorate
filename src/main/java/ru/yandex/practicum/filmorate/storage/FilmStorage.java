package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {
    public long create(Film film);

    public void update(Long id, Film film);

    public void delete(Long id);

    public Film get(Long id);

    List<Film> getPopular(Integer count);

    List<Film> getAll();

    void addLikeFromUser(long filmId, long userId);

    void removeLikeFromUser(long filmId, long userId);

    void addGenresToFilm(long filmId, List<Genre> genres);

    void removeGenresFromFilm(long filmId);
}
