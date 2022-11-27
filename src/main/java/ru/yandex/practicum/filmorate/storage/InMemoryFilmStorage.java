package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    public Map<Long, Film> films = new HashMap<>();
    private Long currentMaxId = 0L;

    @Override
    public long create(Film film) {
        currentMaxId++;
        Long id = currentMaxId;
        film.setId(id);
        films.put(film.getId(), film);
        return id;
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
    public List<Film> getPopular(Integer count) {
        return null;
    }

    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }

    @Override
    public void addLikeFromUser(long filmId, long userId) {

    }

    @Override
    public void removeLikeFromUser(long filmId, long userId) {

    }

    @Override
    public void addGenresToFilm(long filmId, List<Genre> genres) {

    }

    @Override
    public void removeGenresFromFilm(long filmId) {

    }
}
