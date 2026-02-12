//Объект
package org.example.tdlsb.entity;

import lombok.Data;

import java.util.UUID;
@Data
public class ToDoItem {
    private final String id;
    //private final String idOfUser;
    //private final дата создания
    private String title;
    private String description;
    private Integer daysToDo;
    private boolean done = false;

    public ToDoItem(String title, String description, Integer daysToDo) {
        this.id = String.valueOf(UUID.randomUUID());
        this.title = title;
        this.description = description;
        this.daysToDo = daysToDo;
    }


}
