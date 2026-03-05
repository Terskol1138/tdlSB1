//Объект
package org.example.tdlsb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
public class ToDoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    //private final String idOfUser;
    //private final
    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "days_to_do")
    private Integer daysToDo;

    @CreationTimestamp
    @Column(name = "created_at",  updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private boolean done = false;

    public ToDoItem(String title, String description, Integer daysToDo) {
        this.title = title;
        this.description = description;
        this.daysToDo = daysToDo;
        this.done = false;
    }

    public void markAsDone() {
        this.done = true;
        this.completedAt = LocalDateTime.now();
    }


}
