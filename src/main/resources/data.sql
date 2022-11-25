INSERT INTO mpa_rating (id, NAME)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');



INSERT INTO genres (id, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');




INSERT INTO users (email, login, name, birthdate)
VALUES('vasya@example.com', 'vasya', 'vasya', '1971-01-01'),
      ('petya@example.com', 'petya', 'petya', '1972-02-02'),
      ('katya@example.com', 'katya', 'katya', '1973-03-03');


INSERT INTO films (name, description, release_date, duration, mpa_rating)
VALUES ('Охотник на оленей', 'Инженер отдела ИБ в поисках владельцев незапароленной монги, из которой утекли данные', '2009-01-01', 155, 5),
       ('Три идиота', 'Программисты решают переписать банковское ПО на раст втайне от начальства', '2009-01-01', 155, 5),
       ('Тупой против хищника', 'Сатирический хоррор', '1975-05-05', 120, 5),
       ('12 разгневанных мужчин', 'Драма о закрытом пивном ларьке', '2021-01-01', 155, 5),
       ('Остров проклятых', 'На конференции frontend-разработчиков происходит странное', '1978-01-01', 155, 5),
       ('Поймай меня, если сможешь', 'Собака съела флешку с биткоин-кошельком и сбежала', '2009-01-01', 155, 5),
       ('В диких условиях ', 'После масштабной пандемии человечество возвращается к работе в офисах', '2009-01-01', 155, 5),
       ('Бегущий по лезвию ', 'Аллегорическое мокьюментари о незадачливом парне, решившим пообедать шавухой из ларька перед свиданием', '1980-07-07', 90, 5),
       ('Петля времени', 'Да, я ненавижу писать тесты и лучше буду придумывать тупые описания фильмов, чем вот эти все assertEquals :)', '2009-01-01', 155, 5);