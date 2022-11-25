package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Component("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre get(long id) {
        String sqlQuery = "SELECT * FROM genres WHERE ID = ?";
        List<Genre> genres = new ArrayList<>(jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeGenre(resultSet, id), id));

        if (genres.size() != 1) {
            String errorMsg = "";
            if (genres.size() == 0) {
                errorMsg = String.format("User with id = %d not found", id);
                throw new NotFoundException(errorMsg);
            } else {
                errorMsg = String.format("Found more than 1 user with id = %d not found", id);
                throw new RuntimeException(errorMsg);
            }

        }
        return genres.get(0);
    }


    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    private Genre makeGenre(ResultSet resultSet, long rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
