package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long create(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_rating) VALUES (?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);

        return keyHolder.getKey().longValue();
    }

    @Override
    public void update(Long id, Film film) {
        /* можно было бы в "модельке" завести список изменившихся полей и потом только их и обновлять,
         * но наверное, в этом особой необходимости на этом этапе нет */
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());

    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE FROM films WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film get(Long id) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating, m.name mpa_name" +
                " FROM films AS f JOIN mpa_rating AS m ON f.mpa_rating=m.id  WHERE f.id = ?";

        List<Film> films = new ArrayList<>(jdbcTemplate.query(sqlQuery, this::makeFilm, id));
        if (films.size() != 1) {
            String errorMsg;

            if (films.size() == 0) {
                errorMsg = String.format("Film with id = %d not found", id);
                throw new NotFoundException(errorMsg);
            } else {
                errorMsg = String.format("Found more than 1 film with id = %d not found", id);
                throw new RuntimeException(errorMsg);
            }

        }
        return films.get(0);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating, m.name mpa_name" +
                " FROM films AS f JOIN mpa_rating AS m ON f.mpa_rating=m.id LEFT JOIN (SELECT film_id, COUNT(user_id) " +
                "likes FROM likes GROUP BY film_id) l ON f.id=l.film_id ODERDER BY l.likes DESC LIMIT ?";
        return new ArrayList<>(
                jdbcTemplate.query(sqlQuery, this::makeFilm, count));
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating, " +
                "m.name mpa_name FROM films AS f JOIN mpa_rating AS m ON f.mpa_rating=m.id";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    public List<Genre> getFilmGenres(long id) {
        String sqlQuery =
                "SELECT g.id, g.name FROM film_genre f JOIN genres g ON g.id = f.genre_id WHERE f.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, this::makeGenre, id);
    }

    private Genre makeGenre(ResultSet resultSet, long rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public void addLikeFromUser(long filmId, long userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setLong(1, filmId);
            stmt.setLong(2, userId);
            return stmt;
        }, keyHolder);
    }

    public List<Like> getAllLikes() {
        String sqlQuery = "SELECT * FROM likes";
        return jdbcTemplate.query(sqlQuery, this::makeLike);
    }

    private Like makeLike(ResultSet resultSet, long rowNum) throws SQLException {
        return Like.builder()
                .id(resultSet.getLong("id"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .build();
    }

    @Override
    public void removeLikeFromUser(long filmId, long userId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    public void removeAllLikes() {
        String sqlQuery = "DELETE FROM likes";
        jdbcTemplate.update(sqlQuery);
    }

    public void removeGenresFromFilm(long filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public void addGenresToFilm(long filmId, List<Genre> genres) {
        String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)";
        Set<Long> genreIds = genres.stream().map(Genre::getId).collect(Collectors.toSet());
        int batchSize = genreIds.size();

        jdbcTemplate.batchUpdate(sqlQuery,
                genreIds,
                batchSize,
                (PreparedStatement ps, Long genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genreId);
                });
    }


    private Film makeFilm(ResultSet resultSet, long rowNum) throws SQLException {
        MpaRating mpa = MpaRating.builder().
                id(resultSet.getLong("mpa_rating"))
                .name(resultSet.getString("mpa_name"))
                .build();

        long filmId = resultSet.getLong("id");
        return Film.builder()
                .id(filmId)
                .name(resultSet.getString("name"))
                .duration(resultSet.getInt("duration"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpa(mpa)
                .genres(getFilmGenres(filmId))
                .build();
    }
}
