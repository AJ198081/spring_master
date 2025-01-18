package dev.aj.spring_mvc.util;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {CustomerEmailValidator.class})
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CustomEmailPattern {

    String value() default "customer@email.com";

    String message() default "Invalid email address, only gmail.com is allowed";

    Class<?>[] groups() default {};
    Class<? extends java.lang.annotation.Annotation>[] payload() default {};
}
