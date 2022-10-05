package ru.yandex.practicum.filmorate.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;


public class DateValidator implements ConstraintValidator<IsAfter, LocalDate> {

    String validDate;

    @Override
    public void initialize(IsAfter constraintAnnotation) {
        validDate = constraintAnnotation.current();
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        // костыль, по большому счету, но хочется иметь возможность не указывать дату в конструкторе,
        // и при этом как-то валидировать это все внутри
        if (date == null) {
            return false;
        }
        String[] splitDate = validDate.split("-");
        return date.isAfter(LocalDate.of(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[2])));
    }
}

