package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    public Map<Long, Film> films = new HashMap<>();
    int currentMaxId = 0;

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        currentMaxId++;
        int id = currentMaxId;

        film.setId(id);
        films.put(film.getId(), film);

        log.info(String.format("Фильм c id=%d обновлен", film.getId()));
        log.debug(String.format("Значение фильма = %s", film.toString()));
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException(String.format("Фильма с id= %d не найдено", film.getId()));
        }
        films.put(film.getId(), film);

        log.info(String.format("Фильм c id=%d обновлен", film.getId()));
        log.debug(String.format("Значение фильма = %s", film.toString()));
        return film;
    }

    @GetMapping("/films")
    public List<Film> listAllFilms() {
        return List.copyOf(films.values());
    }

}
