package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    Long currentMaxId = 0L;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        currentMaxId++;
        Long id = currentMaxId;
        film.setId(id);
        filmStorage.create(film.getId(), film);

        return film;
    }

    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        checkIfFilmExists(film.getId());
        filmStorage.update(film.getId(), film);

        return film;
    }

    public Film getFilmById(Long id) throws ValidationException, NotFoundException {
        checkIfFilmExists(id);
        return filmStorage.get(id);
    }

    public List<Film> listAllFilms() {
        return filmStorage.getAll();
    }

    public void addLike(Long filmId, Long userId) throws NotFoundException {
        checkIfFilmExists(filmId);
        userStorage.checkIfUserExists(userId);
        filmStorage.get(filmId).addLikeFromUser(userId);
    }

    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        checkIfFilmExists(filmId);
        userStorage.checkIfUserExists(userId);
        filmStorage.get(filmId).removeLikeFromUser(userId);
    }

    public List<Film> getTopLikedFilms(int count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.getAll());
        allFilms.sort(Comparator.comparingInt(Film::getNumberOfLikes).reversed());
        int sliceSize = Math.min(count, allFilms.size());
        return allFilms.subList(0, sliceSize);
    }

    private void checkIfFilmExists(Long filmId) throws NotFoundException {
        if (!filmStorage.contains(filmId)) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден!", filmId));
        }
    }
}
