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
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        filmStorage.create(film);
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
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        filmStorage.get(filmId).addLikeFromUser(userId);
    }

    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        checkIfFilmExists(filmId);
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        filmStorage.get(filmId).removeLikeFromUser(userId);
    }

//    public List<Film> getTopLikedFilms(int count) {
//        List<Film> allFilms = new ArrayList<>(filmStorage.getAll());
//        allFilms.sort(Comparator.comparingInt(Film::getNumberOfLikes).reversed());
//        int sliceSize = Math.min(count, allFilms.size());
//        return allFilms.subList(0, sliceSize);
//    }

    public List<Film> getTopLikedFilms(int count) {
        int sliceSize = Math.min(count, filmStorage.getAll().size());
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingLong(Film::getNumberOfLikes).reversed())
                .collect(Collectors.toList()).subList(0, sliceSize);
    }

    private void checkIfFilmExists(Long filmId) throws NotFoundException {
        Film film = filmStorage.get(filmId);

        if (film == null) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден!", filmId));
        }
    }
}
