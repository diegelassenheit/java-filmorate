package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaStorage {
    MpaRating get(long id);

    List<MpaRating> getAllRatings();
}
