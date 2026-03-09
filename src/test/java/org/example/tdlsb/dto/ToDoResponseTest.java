package org.example.tdlsb.dto;
import org.example.tdlsb.entity.ToDoItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class ToDoResponseTest {

    @Test
    void fromEntity_withFullData_ShouldMapAllFields() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime completedAt = now.plusDays(2);

        ToDoItem entity = new ToDoItem ("Test Title", "Test Description", 5);
        entity.setId("test-id-123");
        entity.setCreatedAt(now);
        entity.setCompletedAt(completedAt);
        entity.setDone(true);

        ToDoResponse response = ToDoResponse.fromEntity(entity);

        assertThat(response.getId()).isEqualTo("test-id-123");
        assertThat(response.getTitle()).isEqualTo("Test Title");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getDaysToDo()).isEqualTo(5);
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getCompletedAt()).isEqualTo(completedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    }

    @Test
    void fromEntity_WithNullEntity_ShouldReturnNull() {
        ToDoResponse response = ToDoResponse.fromEntity(null);
        assertThat(response).isNull();
    }

    @Test
    void fromEntity_WithActiveToDo_ShouldCalculateDaysLeft() {

        LocalDateTime now = LocalDateTime.now();

        ToDoItem entity = new ToDoItem("Test Title:", null, 10);
        entity.setId("test-id-123");
        entity.setCreatedAt(now.minusDays(2));
        entity.setDone(false);

        ToDoResponse response = ToDoResponse.fromEntity(entity);

        assertThat(response.getDaysLeft()).isEqualTo(8);
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getCompletedAt()).isNull();
    }

    @Test
    void fromEntity_WithOverdueToDo_ShouldReturnZeroDaysLeft() {
        LocalDateTime now = LocalDateTime.now();

        ToDoItem entity = new ToDoItem("Test Title:", null, 5);
        entity.setId("test-id-123");
        entity.setCreatedAt(now.minusDays(10));
        entity.setDone(false);

        ToDoResponse response = ToDoResponse.fromEntity(entity);

        assertThat(response.getDaysLeft()).isEqualTo(0);
    }

    @Test
    void fromEntity_WithCompletedToDo_ShouldReturnDaysLeftBasedOnCompletion() {
        LocalDateTime now = LocalDateTime.now();

        ToDoItem entity = new ToDoItem("Test Title:", null, 5);
        entity.setId("test-id-123");
        entity.setCreatedAt(now.minusDays(2));
        entity.setCompletedAt(now);
        entity.setDone(true);

        ToDoResponse response = ToDoResponse.fromEntity(entity);

        assertThat(response.getDaysLeft()).isEqualTo(3);
        assertThat(response.getStatus()).isEqualTo("COMPLETED");


    }

    @Test
    void fromEntity_WithNullDates_ShouldHandleGracefully() {

        ToDoItem entity = new ToDoItem("Test Title", "Test Description", 5);
        entity.setId("test-id-123");
        entity.setCreatedAt(null);
        entity.setCompletedAt(null);
        entity.setDone(false);

        ToDoResponse response = ToDoResponse.fromEntity(entity);

        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getCompletedAt()).isNull();
        assertThat(response.getDeadline()).isNull();
        assertThat(response.getDaysLeft()).isEqualTo(0);
    }

    @Test
    void fromEntity_ShouldFormatDeadLineCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedDeadLine = now.plusDays(5);

        ToDoItem entity = new ToDoItem("Test Title", null, 5);
        entity.setId("test-id-123");
        entity.setCreatedAt(now);
        entity.setDone(false);

        ToDoResponse response = ToDoResponse.fromEntity(entity);

        assertThat(response.getDeadline()).isEqualTo(expectedDeadLine.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }





}