package ru.yandex.practicum.filmorate.validator.interfaces;

import ru.yandex.practicum.filmorate.validator.DateTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeValidator.class)
public @interface DateTimeValidatorCustom {
    public abstract String max() default "";

    public abstract String message() default "Date is not valid";

    public abstract String pattern() default "dd/MM/yyyy";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};

}
