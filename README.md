# java-filmorate

### Create tables для схемы

```SQL
CREATE TYPE mpa_rating AS ENUM ('G', 'PG', 'PG-13', 'R', 'NC-17');

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(320) NOT NULL,
    login VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    birthdate DATE
);

CREATE TABLE films (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR(200) NOT NULL,
    release_date DATE,
    duration serial,
    mpa_rating mpa_rating
);

CREATE TABLE film_genre (
    id BIGSERIAL PRIMARY KEY,
    film_id BIGSERIAL NOT NULL REFERENCES films (id),
    genre_id BIGSERIAL NOT NULL REFERENCES genres (id)
);

CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE likes (
    id SERIAL PRIMARY KEY,
    user_id BIGSERIAL NOT NULL REFERENCES users (id),
    film_id BIGSERIAL NOT NULL REFERENCES films (id)
);

CREATE TABLE friendship (
    id SERIAL PRIMARY KEY,
    user_id_from BIGSERIAL NOT NULL REFERENCES users (id),
    user_id_to BIGSERIAL NOT NULL REFERENCES users (id),
    approved BOOLEAN DEFAULT FALSE
)

```


### Примеры SQL-запросов для описанной схемы данных

#### Пользователь:
```
SELECT * FROM users WHERE user_id=1;
```

#### Лайки пользователя
```
SELECT * FROM likes where user_id=1
```

#### Друзья пользователя:
```
SELECT user_id_from as comb FROM friendship where user_id_to=1 and approved = TRUE
UNION ALL
SELECT user_id_to as comb from friendship where user_id_from=1 and approved = TRUE
```

#### Жанр фильма

```
SELECT genre_id from film_genre WHERE film_id=1
```


ER-диаграмма
![image info](Sprint-11-ERD.png)