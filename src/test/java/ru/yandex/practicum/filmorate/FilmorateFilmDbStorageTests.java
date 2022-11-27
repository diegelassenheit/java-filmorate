package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test_data.sql"})
public class FilmorateFilmDbStorageTests {
    private final FilmDbStorage filmStorage;

    @Test
    public void testGetAllFilms() {
        List<Film> films = filmStorage.getAll();
        assertEquals(9, films.size());
    }

    @Test
    public void testGetFilm() {
        Film film = filmStorage.get(1L);
        assertEquals("Охотник на оленей", film.getName());
        assertEquals("Инженер отдела ИБ в поисках владельцев незапароленной монги, из которой утекли данные",
                film.getDescription());
        assertEquals(LocalDate.of(2009, 1, 1), film.getReleaseDate());
        assertEquals(155, film.getDuration());
        assertEquals(5, film.getMpa().getId());
    }
    
    @Test
    public void testCreateFilm() {
        MpaRating mpa = MpaRating.builder().id(4).build();
        List<Genre> genres = Arrays.asList(Genre.builder().id(1).build(),
                                           Genre.builder().id(2).build(),
                                           Genre.builder().id(3).build());

        Film film = Film.builder().name("Еще один фильм").description("Очень скучный фильм").duration(210)
                .releaseDate(LocalDate.of(1973, 3, 3)).mpa(mpa).genres(genres).build();

        long filmId = filmStorage.create(film);

        List<Film> films = filmStorage.getAll();
        assertEquals(10, films.size());

        assertEquals(filmId, films.get(9).getId());
        Film newFilm = films.get(9);

        assertEquals(film.getName(), newFilm.getName());
        assertEquals(film.getDuration(), newFilm.getDuration());
        assertEquals(film.getDescription(), newFilm.getDescription());
        assertEquals(film.getReleaseDate(), newFilm.getReleaseDate());
        assertEquals(film.getMpa().getId(), newFilm.getMpa().getId());
        
        // и жанры тоже проверим
        filmStorage.addGenresToFilm(filmId, genres);
        List<Genre> filmGenresFromStorage = filmStorage.getFilmGenres(filmId);
        assertEquals(3, filmGenresFromStorage.size());

        assertEquals(1, filmGenresFromStorage.get(0).getId());
        assertEquals("Комедия", filmGenresFromStorage.get(0).getName());

        assertEquals(2, filmGenresFromStorage.get(1).getId());
        assertEquals("Драма", filmGenresFromStorage.get(1).getName());

        assertEquals(3, filmGenresFromStorage.get(2).getId());
        assertEquals("Мультфильм", filmGenresFromStorage.get(2).getName());
    }

    @Test
    public void testUpdateFilm() {
        MpaRating mpa = MpaRating.builder().id(3L).build();
        Film film = Film.builder().id(2L).name("Охотник на оленей-2").description("Новое описание")
                .releaseDate(LocalDate.of(2010, 1, 1)).mpa(mpa).build();

        filmStorage.update(film.getId(), film);

        Film updatedFilm = filmStorage.get(2L);
        assertEquals(film.getName(), updatedFilm.getName());
        assertEquals(film.getDescription(), updatedFilm.getDescription());
        assertEquals(film.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(film.getDuration(), updatedFilm.getDuration());
        assertEquals(film.getMpa().getId(), updatedFilm.getMpa().getId());
    }

    @Test
    public void testDeleteFilm(){
        List<Film> films = filmStorage.getAll();
        assertEquals(9, films.size());
        Set<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toSet());
        assertTrue(filmIds.contains(9L));

        filmStorage.delete(films.get(8).getId());

        List<Film> filmsAfterDelete = filmStorage.getAll();
        assertEquals(8, filmsAfterDelete.size());

        Set<Long> filmIdsAfterDelete = filmsAfterDelete.stream().map(Film::getId).collect(Collectors.toSet());
        assertFalse(filmIdsAfterDelete.contains(9L));
    }

    @Test
    public void testAddLikeFilm(){
        filmStorage.removeAllLikes();
        assertEquals(0, filmStorage.getAllLikes().size());
        filmStorage.addLikeFromUser(1L, 1L);

        List<Like> likes = filmStorage.getAllLikes();
        assertEquals(1, likes.size());
    }

    @Test
    public void testAddAndDeleteLikeFilm(){
        assertEquals(0, filmStorage.getAllLikes().size());
        filmStorage.addLikeFromUser(1L, 1L);

        List<Like> likes = filmStorage.getAllLikes();
        assertEquals(1, likes.size());
        assertEquals(likes.get(0).getFilmId(), 1L);
        assertEquals(likes.get(0).getUserId(), 1L);

        filmStorage.removeLikeFromUser(1L, 1L);
        List<Like> likesAfterDelete = filmStorage.getAllLikes();
        assertEquals(0, likesAfterDelete.size());
    }

    @Test
    public void testFilmRatings(){
        filmStorage.addLikeFromUser(1L, 1L);

        filmStorage.addLikeFromUser(2L, 1L);
        filmStorage.addLikeFromUser(2L, 2L);

        filmStorage.addLikeFromUser(3L, 1L);
        filmStorage.addLikeFromUser(3L, 2L);
        filmStorage.addLikeFromUser(3L, 3L);

        // Топ-1
        List<Film> topFilms = filmStorage.getPopular(1);
        assertEquals(1, topFilms.size());
        assertEquals(3L, topFilms.get(0).getId());
        assertEquals("Тупой против хищника", topFilms.get(0).getName());

        // Топ-2
        List<Film> topTwoFilms = filmStorage.getPopular(3);
        assertEquals(3, topTwoFilms.size());

        assertEquals(3L, topTwoFilms.get(0).getId());
        assertEquals("Тупой против хищника", topFilms.get(0).getName());

        // Топ-3
        List<Film> topThreeFilms = filmStorage.getPopular(3);
        assertEquals(3, topThreeFilms.size());

        assertEquals(3L, topThreeFilms.get(0).getId());
        assertEquals("Тупой против хищника", topThreeFilms.get(0).getName());

        assertEquals(2L, topThreeFilms.get(1).getId());
        assertEquals("Три идиота", topThreeFilms.get(1).getName());

        assertEquals(1L, topThreeFilms.get(2).getId());
        assertEquals("Охотник на оленей", topThreeFilms.get(2).getName());

        // Топ-4: фильмов всего три, но ошибки не будет, получим три фильма
        List<Film> topFourFilms = filmStorage.getPopular(3);
        assertEquals(3, topFourFilms.size());

        assertEquals(3L, topFourFilms.get(0).getId());
        assertEquals("Тупой против хищника", topFourFilms.get(0).getName());

        assertEquals(2L, topFourFilms.get(1).getId());
        assertEquals("Три идиота", topFourFilms.get(1).getName());

        assertEquals(1L, topFourFilms.get(2).getId());
        assertEquals("Охотник на оленей", topFourFilms.get(2).getName());
    }
}    