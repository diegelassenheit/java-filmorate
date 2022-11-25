package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmorateUserDbStorageTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testCreateUser() {
        User userOleg = User.builder().email("oleg@example.com").name("oleg")
                .login("oleg").birthday(LocalDate.of(1978, 12, 12)).build();

        userStorage.create(userOleg);
        User userOlegFromStorage = userStorage.getUser(4L);
        assertThat(userOlegFromStorage).hasFieldOrPropertyWithValue("login", "oleg");
        assertThat(userOlegFromStorage).hasFieldOrPropertyWithValue("name", "oleg");
        assertThat(userOlegFromStorage).hasFieldOrPropertyWithValue("email", "oleg@example.com");


    }


    @Test
    public void testUpdateUser() {
        // Поменяем почту
        User userKatya = User.builder().id(3L).email("katya-new-email@example.com").name("katya")
                .login("katya").birthday(LocalDate.of(1973, 3, 3)).build();

        userStorage.update(3L, userKatya);
        User userKatyaFromStorage = userStorage.getUser(3L);
        assertThat(userKatyaFromStorage).hasFieldOrPropertyWithValue("login", "katya");
        assertThat(userKatyaFromStorage).hasFieldOrPropertyWithValue("name", "katya");
        assertThat(userKatyaFromStorage).hasFieldOrPropertyWithValue("email", "katya-new-email@example.com");

    }


    @Test
    public void testGetUser() {
        User user1 = userStorage.getUser(1L);
        assertEquals("vasya", user1.getName());
        assertEquals("vasya", user1.getLogin());
        assertEquals("vasya@example.com", user1.getEmail());

        User user2 = userStorage.getUser(2L);
        assertEquals("petya", user2.getName());
        assertEquals("petya", user2.getLogin());
        assertEquals("petya@example.com", user2.getEmail());

        User user3 = userStorage.getUser(3L);
        assertEquals("katya", user3.getName());
        assertEquals("katya", user3.getLogin());
        assertEquals("katya-new-email@example.com", user3.getEmail());
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userStorage.getAllUsers();
        assertThat(users).asList().size().isEqualTo(3);

        assertThat(users.get(0)).hasFieldOrPropertyWithValue("login", "vasya");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("name", "vasya");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("email", "vasya@example.com");

        assertThat(users.get(1)).hasFieldOrPropertyWithValue("login", "petya");
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("name", "petya");
        assertThat(users.get(1)).hasFieldOrPropertyWithValue("email", "petya@example.com");

        assertThat(users.get(2)).hasFieldOrPropertyWithValue("login", "katya");
        assertThat(users.get(2)).hasFieldOrPropertyWithValue("name", "katya");
        assertThat(users.get(2)).hasFieldOrPropertyWithValue("email", "katya@example.com");
    }

    @Test
    public void testDeleteUser() {
        userStorage.delete(3L);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            userStorage.getUser(3L);
        });
        assertEquals("User with id = 3 not found", exception.getMessage());
    }


    @Test
    public void testDeleteFriend() {
        userStorage.addFriend(1L, 2L);
        List<User> userFriends = userStorage.getAllFriends(1L);
        assertEquals(1, userFriends.size());

        assertEquals(userFriends.get(0).getId(), 2L);
        assertEquals(userFriends.get(0).getEmail(), "petya@example.com");
        assertEquals(userFriends.get(0).getLogin(), "petya");
        assertEquals(userFriends.get(0).getName(), "petya");

        userStorage.removeFriend(1L, 2L);
    }

    @Test
    public void testAddFriend() {
        userStorage.addFriend(1L, 2L);
        List<User> userFriends = userStorage.getAllFriends(1L);
        assertEquals(1, userFriends.size());

        assertEquals(userFriends.get(0).getId(), 2L);
        assertEquals(userFriends.get(0).getEmail(), "petya@example.com");
        assertEquals(userFriends.get(0).getLogin(), "petya");
        assertEquals(userFriends.get(0).getName(), "petya");

        // но второй юзер первого в друзья не добавлял, поэтому у него список друзей пустой
        List<User> userFriends1 = userStorage.getAllFriends(2L);
        assertEquals(0, userFriends1.size());
    }
}
