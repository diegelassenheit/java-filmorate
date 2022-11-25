package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Component("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MpaRating get(long id) {
        String sqlQuery = "SELECT * FROM mpa_rating WHERE ID = ?";
        List<MpaRating> mpa_ratings = new ArrayList<>(jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeMpaRating(resultSet, id), id));

        if (mpa_ratings.size() != 1) {
            String errorMsg = "";
            if (mpa_ratings.size() == 0) {
                errorMsg = String.format("User with id = %d not found", id);
                throw new NotFoundException(errorMsg);
            } else {
                errorMsg = String.format("Found more than 1 user with id = %d not found", id);
                throw new RuntimeException(errorMsg);
            }

        }
        return mpa_ratings.get(0);
    }

    @Override
    public List<MpaRating> getAllRatings() {
        String sqlQuery = "SELECT * FROM mpa_rating";
        return jdbcTemplate.query(sqlQuery, this::makeMpaRating);
    }

    private MpaRating makeMpaRating(ResultSet resultSet, long rowNum) throws SQLException {
        return MpaRating.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
