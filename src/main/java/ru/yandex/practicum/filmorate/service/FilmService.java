package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        long filmId = filmStorage.create(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmStorage.addGenresToFilm(filmId, film.getGenres());
        }
        return film;
    }

    public Film updateFilm(Film film) {
        checkIfFilmExists(film.getId());
        filmStorage.update(film.getId(), film);

        long filmId = film.getId();
        // каждый раз удалять и заново добавлять не очень красиво, но опять же, отслеживания изменившихся полей нет,
        // а делать запросы в базу, чтобы понять, что изменилось по сравнению с новыми данными - кажется оверхедом
        // логики больше, а выигрываем мы всего один запрос в несложную таблицу. Но тут спорно.
        if (film.getGenres() != null) {
            filmStorage.removeGenresFromFilm(filmId);
            filmStorage.addGenresToFilm(filmId, film.getGenres());
        }

        // Читать заново фильм тоже криво. В запросе нам прилетают только айди жанров,
        // а в тестах от нас ждут еще и их имена. И жанры в тестах бывают с дубликатами, и мы удаляем лишнее.
        // Значит, надо из метода с добавлением жанров возвращать
        // фактический список жанров - то есть надо делать селект из базы, чтобы подтянуть их имена.
        // А раз уже есть лишний запрос, то кажется не очень принципиальным - заново прочитать фильм или подтягивать жанры.
        // Хотя селект с джоином из таблицы с фильмами будет тяжелее, чем селект из таблицы с жанрами.
        // А еще можно было какой-нибудь кэш жанров сделать, раз мы их не редактируем. А даже если бы редактировали -
        // можно было было бы при апдетах обновлять и кэш. Но мороки много, так что оставил пока так.
        return getFilmById(filmId);
    }

    public Film getFilmById(Long id) {
        checkIfFilmExists(id);
        return filmStorage.get(id);
    }

    public List<Film> listAllFilms() {
        return filmStorage.getAll();
    }

    public void addLike(Long filmId, Long userId) {
        checkIfFilmExists(filmId);
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        filmStorage.addLikeFromUser(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        checkIfFilmExists(filmId);
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        filmStorage.removeLikeFromUser(filmId, userId);
    }

    public List<Film> getTopLikedFilms(int count) {
        return filmStorage.getPopular(count);
    }

    private void checkIfFilmExists(Long filmId) {
        Film film = filmStorage.get(filmId);

        if (film == null) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден!", filmId));
        }
    }
}
