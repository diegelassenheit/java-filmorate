package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Sql({"/schema.sql", "/test_data.sql"})
class FilmorateApplicationTests {
    public ConfigurableApplicationContext ctx;

    @LocalServerPort

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:s");
    private static Gson gson = getGson();

    @Test
    void contextLoads() {
    }

    // Я не уверен, что это правильный способ запускать-останавливать приложение. Но это самое лаконичное из всего,
    // что я наглил без моков, mvc и прочего.
    @BeforeEach
    public void setUp() {
        String[] args = new String[0];
        ctx = SpringApplication.run(FilmorateApplication.class, args);
    }

    @AfterEach
    public void cleanUp() {
        ctx.close();
    }

    @Test
    void emptyCreateFilmRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");
        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString("{}");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void okCreateFilmRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма 1");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2012, 12, 12));
        film.setMpa(MpaRating.builder().id(1).build());

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Film responseFilm = gson.fromJson(response.body(), Film.class);
        assertEquals(film.getName(), responseFilm.getName());
        assertEquals(film.getDuration(), responseFilm.getDuration());
        assertEquals(film.getReleaseDate(), responseFilm.getReleaseDate());
    }

    @Test
    void invalidDataCreateFilmRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма 2");
        film.setDuration(-1);
        film.setReleaseDate(LocalDate.of(2012, 12, 12));

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void emptyUpdateFilmRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма 3");
        film.setDuration(-1);
        film.setReleaseDate(LocalDate.of(2012, 12, 12));

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString("{}");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void okUpdateFilmRequestTest() throws IOException, InterruptedException {
        // создаем фильм. Пока через веб, раз нет базы и не получится просто туда подложить
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма 4");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2012, 12, 12));
        film.setMpa(MpaRating.builder().id(1).build());

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Film responseFilm = gson.fromJson(response.body(), Film.class);
        assertEquals(film.getName(), responseFilm.getName());
        assertEquals(film.getDuration(), responseFilm.getDuration());
        assertEquals(film.getReleaseDate(), responseFilm.getReleaseDate());

        // а теперь уже апдейтим
        film.setDescription("Описание фильма 4 - исправленное");
        film.setId(responseFilm.getId());
        final HttpRequest.BodyPublisher updateBody = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
        HttpRequest updateRequest = HttpRequest.newBuilder().uri(url).PUT(updateBody).header("Content-Type", "application/json").build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updateResponse.statusCode());

        Film updateResponseFilm = gson.fromJson(response.body(), Film.class);
        assertEquals(film.getDuration(), updateResponseFilm.getDuration());
    }

    @Test
    void invalidUpdateFilmRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма 5");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2012, 12, 12));
        film.setMpa(MpaRating.builder().id(1).build());

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Film responseFilm = gson.fromJson(response.body(), Film.class);
        assertEquals(film.getName(), responseFilm.getName());
        assertEquals(film.getDuration(), responseFilm.getDuration());
        assertEquals(film.getReleaseDate(), responseFilm.getReleaseDate());

        // а теперь уже апдейтим
        film.setDuration(-1);
        final HttpRequest.BodyPublisher updateBody = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
        HttpRequest updateRequest = HttpRequest.newBuilder().uri(url).POST(updateBody).header("Content-Type", "application/json").build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, updateResponse.statusCode());

    }

    @Test
    void getAllFilmsNoFilmRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<List<Film>>() {
        }.getType();
        String resp = response.body();
        List<Film> filmsResponse = gson.fromJson(resp, listType);

        assertEquals(0, filmsResponse.size());
    }

    @Test
    void getAllFilmsTwoFilmRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");

        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание фильма 1");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2012, 12, 12));
        film.setMpa(MpaRating.builder().id(1).build());

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Film film1 = new Film();
        film1.setName("Фильм");
        film1.setDescription("Описание фильма 1");
        film1.setDuration(90);
        film1.setReleaseDate(LocalDate.of(2012, 12, 12));
        film1.setMpa(MpaRating.builder().id(1).build());

        final HttpRequest.BodyPublisher createBody1 = HttpRequest.BodyPublishers.ofString(gson.toJson(film1));
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(createBody1).header("Content-Type", "application/json").build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());


        client = HttpClient.newHttpClient();

        HttpRequest requestGet = HttpRequest.newBuilder().uri(url).GET().header("Content-Type", "application/json").build();

        HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet.statusCode());

        Type listType = new TypeToken<List<Film>>() {
        }.getType();
        String resp = responseGet.body();
        List<Film> filmsResponse = gson.fromJson(resp, listType);

        assertEquals(2, filmsResponse.size());
    }

    @Test
    void emptyCreateUserRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");
        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString("{}");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }


    @Test
    void okCreateUserRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        User user = new User();
        user.setName("Vasya");
        user.setBirthday(LocalDate.of(1970, 1, 2));
        user.setLogin("vasya1970");
        user.setEmail("vasya@yandex.ru");

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        User responseUser = gson.fromJson(response.body(), User.class);
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getLogin(), responseUser.getLogin());
        assertEquals(user.getBirthday(), responseUser.getBirthday());
        assertEquals(user.getEmail(), responseUser.getEmail());
    }

    @Test
    void invalidCreateUserRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        User user = new User();
        user.setName("Vasya");
        user.setBirthday(LocalDate.of(1970, 1, 2));
        user.setLogin("vasya1970");
        user.setEmail("vasyayandex.ru"); // без символа собаки валидация не должна пройти

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void okUpdateUserRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        User user = new User();
        user.setName("Vasya");
        user.setBirthday(LocalDate.of(1970, 1, 2));
        user.setLogin("vasya1970");
        user.setEmail("vasya@yandex.ru");

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        User responseUser = gson.fromJson(response.body(), User.class);
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getLogin(), responseUser.getLogin());
        assertEquals(user.getBirthday(), responseUser.getBirthday());
        assertEquals(user.getEmail(), responseUser.getEmail());

        user.setName("VasyaVasya");
        user.setId(responseUser.getId());

        final HttpRequest.BodyPublisher updateBody = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(url).PUT(updateBody).header("Content-Type", "application/json").build();

        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseUpdate.statusCode());

        responseUser = gson.fromJson(responseUpdate.body(), User.class);
        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getLogin(), responseUser.getLogin());
        assertEquals(user.getBirthday(), responseUser.getBirthday());
        assertEquals(user.getEmail(), responseUser.getEmail());

    }

    @Test
    void invalidUpdateUserRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        User user = new User();
        user.setName("Vasya");
        user.setBirthday(LocalDate.of(1970, 1, 2));
        user.setLogin("vasya1970");
        user.setEmail("vasyayandex.ru"); // убедимся, что валидация на емейл работает и при апдейте

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void emptyUpdateUserRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson("{}"));
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void getAllUsersEmptyUsersRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getAllUsersTwoUsersRequestTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");

        // User 1
        User user = new User();
        user.setName("Vasya");
        user.setBirthday(LocalDate.of(1970, 1, 2));
        user.setLogin("vasya1970");
        user.setEmail("vasya@yandex.ru");

        final HttpRequest.BodyPublisher createBody = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(createBody).header("Content-Type", "application/json").build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        User responseUser = gson.fromJson(response.body(), User.class);
        user.setId(responseUser.getId());

        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getLogin(), responseUser.getLogin());
        assertEquals(user.getBirthday(), responseUser.getBirthday());
        assertEquals(user.getEmail(), responseUser.getEmail());

        // User 2
        User user2 = new User();
        user2.setName("Vasya1");
        user2.setBirthday(LocalDate.of(1970, 1, 3));
        user2.setLogin("vasya11970");
        user2.setEmail("vasya1@yandex.ru");

        final HttpRequest.BodyPublisher createBody2 = HttpRequest.BodyPublishers.ofString(gson.toJson(user2));
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(createBody2).header("Content-Type", "application/json").build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        User responseUser2 = gson.fromJson(response2.body(), User.class);
        user2.setId(responseUser2.getId());

        assertEquals(user2.getName(), responseUser2.getName());
        assertEquals(user2.getLogin(), responseUser2.getLogin());
        assertEquals(user2.getBirthday(), responseUser2.getBirthday());
        assertEquals(user2.getEmail(), responseUser2.getEmail());

        // А теперь проверяем список
        HttpRequest requestList = HttpRequest.newBuilder().uri(url).GET().header("Content-Type", "application/json").build();

        HttpResponse<String> responseList = client.send(requestList, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type listType = new TypeToken<List<User>>() {
        }.getType();
        String resp = responseList.body();
        List<User> usersResponse = gson.fromJson(resp, listType);

        assertEquals(2, usersResponse.size());
        assertEquals(usersResponse.get(0).getId(), user.getId());
        assertEquals(usersResponse.get(0).getLogin(), user.getLogin());
        assertEquals(usersResponse.get(0).getBirthday(), user.getBirthday());
        assertEquals(usersResponse.get(0).getEmail(), user.getEmail());
        assertEquals(usersResponse.get(0).getName(), user.getName());

        assertEquals(usersResponse.get(1).getId(), user2.getId());
        assertEquals(usersResponse.get(1).getLogin(), user2.getLogin());
        assertEquals(usersResponse.get(1).getBirthday(), user2.getBirthday());
        assertEquals(usersResponse.get(1).getEmail(), user2.getEmail());
        assertEquals(usersResponse.get(1).getName(), user2.getName());
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        return gsonBuilder.create();
    }

    public static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
            if (localDate != null) {
                jsonWriter.value(localDate.format(DATETIME_FORMATTER));
            } else {
                jsonWriter.value("null");
            }
        }

        @Override
        public LocalDate read(final JsonReader jsonReader) throws IOException {
            String val = jsonReader.nextString();
            if (!val.equals("null")) {
                return LocalDate.parse(val, DATETIME_FORMATTER);
            } else {
                return null;
            }
        }
    }
}
