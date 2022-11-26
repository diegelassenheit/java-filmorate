package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private static class Friendship {
        private long id;
        private long user_from;
        private long user_to;
    }

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthdate) VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        long userId = keyHolder.getKey().longValue();
        user.setId(userId);

        return user;
    }

    @Override
    public User update(Long id, User user) {
        String sqlQuery = "UPDATE users SET email =?, login = ?, name = ?, birthdate = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "DELETE FROM users WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, id);

    }

    @Override
    public User getUser(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE ID = ?";
        List<User> users = new ArrayList<>(jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeUser(resultSet, id), id));

        if (users.size() != 1) {
            String errorMsg = "";
            if (users.size() == 0) {
                errorMsg = String.format("User with id = %d not found", id);
                throw new NotFoundException(errorMsg);
            } else {
                errorMsg = String.format("Found more than 1 user with id = %d not found", id);
                throw new RuntimeException(errorMsg);
            }
        }
        return users.get(0);
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public boolean contains(Long key) {
        return false;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO friendship (user_id_from, user_id_to) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlQuery = "DELETE FROM friendship WHERE user_id_from = ? AND user_id_to = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(long userId) {
        String sqlQuery = "SELECT * FROM users where id in (SELECT user_id_to FROM friendship WHERE user_id_from = ?)";
        List<User> users = new ArrayList<>(jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeUser(resultSet, userId), userId));
        return users;
    }


    public List<Friendship> getAllFriendships() {
        String sqlQuery = "SELECT * FROM friendship";
        List<Friendship> friendships = new ArrayList<>(jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> makeFriendship(resultSet, 0)));
        return friendships;
    }

    @Override
    public List<User> getMutalFriends(long userId) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {

        return null;
    }


    private User makeUser(ResultSet resultSet, long rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthdate").toLocalDate())
                .build();
    }

    private Friendship makeFriendship(ResultSet resultSet, long rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.user_from = resultSet.getLong("user_id_from");
        friendship.user_to = resultSet.getLong("user_id_to");
        return friendship;
    }

}
