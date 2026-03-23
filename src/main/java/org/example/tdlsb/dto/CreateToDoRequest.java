package org.example.tdlsb.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tdlsb.entity.ToDoItem;
import org.example.tdlsb.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateToDoRequest {

    @NotBlank
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters!")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters!")
    private String description;

    @NotNull(message = "Days to complete is required")
    @Min(value = 1, message = "Days must be at  least 1")
    @Max(value = 365, message = "Days cannot exceed 365")
    private Integer daysToDo;

    public ToDoItem toEntity(User user) {
        ToDoItem todo = new ToDoItem();
        todo.setTitle(this.title);
        todo.setDescription(this.description);
        todo.setDaysToDo(this.daysToDo);
        todo.setDone(false);
        todo.setUser(user);
        return todo;
    }
}
