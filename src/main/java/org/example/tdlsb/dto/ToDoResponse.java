package org.example.tdlsb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tdlsb.entity.ToDoItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToDoResponse {

    private String id;
    private String title;
    private String description;
    private String status;
    private String createdAt;
    private String completedAt;
    private Integer daysToDo;
    private Long daysLeft;
    private String deadline;

    public static ToDoResponse fromEntity(ToDoItem entity) {
        if (entity == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = null;
        if (entity.getCreatedAt() != null && entity.getDaysToDo() != null) {
            deadline = entity.getCreatedAt().plusDays(entity.getDaysToDo());
        }

        long daysLeft = 0;
        if (deadline != null) {
            daysLeft = ChronoUnit.DAYS.between(now.toLocalDate(), deadline.toLocalDate());
            daysLeft = Math.max(0, daysLeft);
        }

        return ToDoResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.isDone() ? "COMPLETED" : "ACTIVE")
                .createdAt(formatDateTime(entity.getCreatedAt()))
                .completedAt(formatDateTime(entity.getCompletedAt()))
                .daysToDo(entity.getDaysToDo())
                .daysLeft(Math.max(0, daysLeft))
                .deadline(formatDateTime(deadline))
                .build();

    }

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
