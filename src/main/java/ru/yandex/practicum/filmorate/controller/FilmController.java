package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        film = filmService.createFilm(film);
        log.info(String.format("Фильм c id=%d обновлен", film.getId()));
        log.debug(String.format("Значение фильма = %s", film.toString()));
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        film = filmService.updateFilm(film);
        log.info(String.format("Фильм c id=%d обновлен", film.getId()));
        log.debug(String.format("Значение фильма = %s", film.toString()));
        return film;
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable("id") Long id) {
        Film film = filmService.getFilmById(id);
        log.info(String.format("Фильм c id=%d обновлен", film.getId()));
        log.debug(String.format("Значение фильма = %s", film.toString()));
        return film;
    }

    @GetMapping("/films")
    public List<Film> listAllFilms() {
        return filmService.listAllFilms();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> addFilmLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
        log.info(String.format("Добавлен лайк фильму id=%d от пользователя %d", id, userId));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity<?> removeFilmLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
        log.info(String.format("Удален лайк фильму id=%d от пользователя %d", id, userId));
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count
    ) {
        List<Film> topFilms = filmService.getTopLikedFilms(count);
        log.info(String.format("Получены %d самых залайканных фильмов %s", count, topFilms.toString()));
        return topFilms;
    }
}
