package org.example.tdlsb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class CreateToDoRequestTest {

    private static Validator validator;
    private CreateToDoRequest request;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();

        }
    }

    @BeforeEach
    void setUp() {

        request = CreateToDoRequest.builder()
                .title("Valid Title")
                .description("Valid Description")
                .daysToDo(5)
                .build();
    }

    @Test
    void validate_WithValidData_ShuldHaveNoViolations() {
        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_WithBlankTitle_ShouldHaveViolation() {

        request.setTitle(" ");
        request.setDescription("Description");
        request.setDaysToDo(5);

        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
        assertThat(violations)
                .allMatch(v -> v.getPropertyPath().toString().equals("title"));

        assertThat(violations)
                .anyMatch(v -> v.getMessage().contains("between 3 and 100"));
    }

    @Test
    void validate_WithTitleTooLong_ShouldHaveViolation() {

        request.setTitle("a".repeat(102));

        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Title must be between 3 and 100 characters!");
    }

    @Test
    void validate_WithNullDescription_ShouldHaveNoViolation() {
        request.setDescription(null);

        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_WithDescriptionTooLong_ShouldHaveViolation() {
        request.setDescription("a".repeat(1002));

        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Description cannot exceed 1000 characters!");
    }

    @Test
    void validate_WithNullDaysToDo_ShouldHaveViolation() {
        request.setDaysToDo(null);

        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Days to complete is required");
    }

    @Test
    void validate_WithDaysToDoLessThanNine_ShouldHaveViolation() {
        request.setDaysToDo(0);

        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Days must be at  least 1");
    }

    @Test
    void validate_WithDaysToDoMoreThanMax_ShouldHaveViolation() {
        request.setDaysToDo(366);

        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Days cannot exceed 365");
    }

    @Test
    void validate_WithDaysToDoMin_ShouldHaveNoViolations() {
        // Arrange
        request.setDaysToDo(1);

        // Act
        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void validate_WithDaysToDoMax_ShouldHaveNoViolations() {
        // Arrange
        request.setDaysToDo(365);

        // Act
        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void validate_WithMultipleInvalidFields_ShouldHaveMultipleViolations() {
        // Arrange
        request.setTitle(""); // пустой
        request.setDescription("a".repeat(1001)); // слишком длинный
        request.setDaysToDo(0); // меньше минимума

        // Act
        Set<ConstraintViolation<CreateToDoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(4); // три поля не прошли валидацию
    }

}