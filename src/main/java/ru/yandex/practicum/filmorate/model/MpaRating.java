package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class MpaRating {
    private long id;
    private String name;
}
