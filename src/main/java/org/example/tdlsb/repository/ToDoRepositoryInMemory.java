//Пережиток из прошлого, подлежит сносу после добавления БД
package org.example.tdlsb.repository;

import org.example.tdlsb.entity.ToDoItem;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ToDoRepositoryInMemory implements ToDoRepository {

    private final Map<String, ToDoItem> storage = new HashMap<>();

    @Override
    public ToDoItem save(ToDoItem item) {
        storage.put(item.getId(), item);
        return item;
    }
    @Override
    public ToDoItem findById(String id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            System.out.println("Не существует задачи с id: " + id);
            return null;
        }

    }

    @Override
    public ToDoItem update(ToDoItem item) {
        if (storage.containsKey(item.getId())) {
            storage.put(item.getId(), item);
            return item;
        } else {
            System.out.println("Такой задачи не существует!");
            return null;
        }

    }

    public void deleteById(String id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
        } else {
            System.out.println("Запись не найдена!");
        }
    }

    public List<ToDoItem> findAll() {
        return List.copyOf(storage.values());
    }
}

