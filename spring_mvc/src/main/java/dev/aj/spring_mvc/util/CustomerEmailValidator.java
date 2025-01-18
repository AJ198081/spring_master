package dev.aj.spring_mvc.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerEmailValidator implements ConstraintValidator<CustomEmailPattern, String> {

    private String value;

    @Value("${customer.email.valid.domains: gmail.com,google.com}")
    private List<String> validDomains;

    @Override
    public void initialize(CustomEmailPattern constraintAnnotation) {
        value = constraintAnnotation.value();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null) {
            return false;
        }
        return validDomains.stream()
                .anyMatch(email::endsWith);
    }
}
