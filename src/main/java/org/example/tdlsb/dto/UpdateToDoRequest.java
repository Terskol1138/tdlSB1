package org.example.tdlsb.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tdlsb.entity.ToDoItem;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateToDoRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Min(value = 1, message = "Days must be at least 1")
    @Max(value = 365, message = "Days cannot exceed 365")
    private Integer daysToDo;

    public void updateEntity(ToDoItem entity) { //логи?! Надо ли, или все логи делаются в сервисе?
        if (title != null) {
            entity.setTitle(title);
        }
        if (description != null) {
            entity.setDescription(description);
        }
        if (daysToDo != null) {
            entity.setDaysToDo(daysToDo);
        }
    }
}
