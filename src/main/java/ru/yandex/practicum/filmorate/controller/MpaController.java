package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService genreService) {
        this.mpaService = genreService;
    }

    @GetMapping("/mpa")
    @ResponseStatus(HttpStatus.OK)
    public List<MpaRating> getAllRatings() {
        return mpaService.getAllRatings();
    }

    @GetMapping("/mpa/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MpaRating getRatingById(@PathVariable long id) {
        return mpaService.get(id);
    }
}
