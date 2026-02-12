//Интерфейс взаимодействия с данными
package org.example.tdlsb.repository;
import org.example.tdlsb.entity.ToDoItem;

import java.util.List;
public interface ToDoRepository {
    ToDoItem findById(String id);
    List<ToDoItem> findAll();
    ToDoItem save(ToDoItem item);
    ToDoItem update(ToDoItem item);
    void deleteById(String id);
}
