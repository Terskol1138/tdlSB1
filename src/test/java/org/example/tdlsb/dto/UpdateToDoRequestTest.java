package org.example.tdlsb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateToDoRequestTest {

    private static Validator validator;
    private UpdateToDoRequest request;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUp() {
        request = UpdateToDoRequest.builder().build(); // пустой запрос
    }

    @Test
    void validate_WithAllFieldsNull_ShouldHaveNoViolations() {
        // Act
        Set<ConstraintViolation<UpdateToDoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void validate_WithValidTitle_ShouldHaveNoViolations() {
        // Arrange
        request.setTitle("Valid Title");

        // Act
        Set<ConstraintViolation<UpdateToDoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void validate_WithTitleTooShort_ShouldHaveViolation() {
        // Arrange
        request.setTitle("ab");

        // Act
        Set<ConstraintViolation<UpdateToDoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("between 3 and 100");
    }

    @Test
    void validate_WithDaysToDoInvalid_ShouldHaveViolation() {
        // Arrange
        request.setDaysToDo(400);

        // Act
        Set<ConstraintViolation<UpdateToDoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("cannot exceed 365");
    }

    @Test
    void validate_WithMultipleInvalidFields_ShouldHaveMultipleViolations() {
        // Arrange
        request.setTitle("a"); // слишком короткий
        request.setDescription("a".repeat(1001)); // слишком длинный
        request.setDaysToDo(0); // меньше минимума

        // Act
        Set<ConstraintViolation<UpdateToDoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(3);
    }
}