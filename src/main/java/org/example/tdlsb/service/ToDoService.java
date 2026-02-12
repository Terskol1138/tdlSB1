/*Сервис с логикой ToDo.
//Рассмотреть необходимые правки, вроде, он должен безболезненно пройти в новый вариант
Добавить взаимодействие со временем
DTO!!!!*/
package org.example.tdlsb.service;

import org.example.tdlsb.entity.ToDoItem;
import org.example.tdlsb.repository.ToDoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToDoService {
    private final ToDoRepository repository;
    public ToDoService(ToDoRepository repository) {
        this.repository = repository;
    }

    //Переписать под создание объекта, его проверку, ок -> save();
    @Deprecated
    public ToDoItem createToDo(ToDoItem toDoItem) {
        String title = toDoItem.getTitle();
        String description = toDoItem.getDescription();
        if (title == null || description == null) {
            System.out.println("Нельзя создавать пустую задачу!");
        } else {
            repository.save(toDoItem);
        }
        return null;
    }

    //Получает название, запрашивает findAll, фильтрует, возвращает List
    public List<ToDoItem> findByTitle(String title) {
        List<ToDoItem> allItems = repository.findAll();
        List<ToDoItem> toDoItemsByTitle = allItems.stream()
                .filter(item -> item.getTitle() != null
                        && item.getTitle().equalsIgnoreCase(title.trim()))
                .collect(Collectors.toList());

        return toDoItemsByTitle;
    }

    //Переписать!
    @Deprecated
    public ToDoItem markAsDone(List <ToDoItem> activeToDo, String input) {
        ArrayList<ToDoItem> markAsDone = new ArrayList<>();
        int l = activeToDo.size();

        if (input == null || input.equals("")) {

        } else {
            List<Integer> numbers = Arrays.stream(input.split(",\\s*"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            for (Integer number : numbers) {
                if (number <= l) {
                    markAsDone.add(activeToDo.get(number - 1));//Подумать, надо ли исключение, вроде все безопасно!!

                } else {
                    System.out.println("Слишком большое число!");
                }
            }
        }
        for (ToDoItem toDoItem : markAsDone) {
            repository.update(toDoItem);
        }
        return null;
    }

    //Получает запрос от представления, вызывает findAll(), возвращает незавершенные (пустой список?)
    public List<ToDoItem> showActive() {
        List<ToDoItem> allItems = repository.findAll();
        List<ToDoItem> activeToDo = allItems.stream()
                .filter(item -> item.isDone() == false)
                .collect(Collectors.toList());
        return activeToDo;
    }

    //Получает запрос от представления, вызывает findAll(), возвращает завершенные (пустой список?)

    public List<ToDoItem> showCompleted() {
        List<ToDoItem> allItems = repository.findAll();
        List<ToDoItem> completedToDo = allItems.stream()
                .filter(item -> item.isDone() == true)
                .collect(Collectors.toList());
        return completedToDo;
    }
    //Получает запрос от представления, вызывает findAll(), возвращает список, если есть

    public List<ToDoItem> showAll() {
        List<ToDoItem> allItems = repository.findAll();
        return allItems;
    }

    //Отметить выполненным - переписать, см. контроллер.

    // Изменить дедлайн (см. контроллер)

    //Удалить задачу (см. контроллер)
}
