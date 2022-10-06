package ru.yandex.practicum.filmorate.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface IsAfter {
    // честно нагуглил такую валидацию. Я даже не уверен, что до конца понимаю, что тут происходит на самом деле
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String message() default "Дата релиза не должна быть раньше 28 декабря 1985 года";

    String current();
}
