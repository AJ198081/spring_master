package dev.aj.full_stack_v3.domain.dto.validations;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

@Constraint(validatedBy = NoMoreThanAYearInFuture.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoMoreThanAYearInFuture {

    String message() default "Date must be no more than a year in the future";

    Class<?>[] groups() default {};
    Class<? extends LocalDate>[] payload() default {};

    class Validator implements ConstraintValidator<NoMoreThanAYearInFuture, LocalDate> {

        @Override
        public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            LocalDate maxAllowedDate = LocalDate.now().plusDays(1);
            return !value.isAfter(maxAllowedDate);
        }

    }
}
