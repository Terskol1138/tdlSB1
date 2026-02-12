/*HTTP - взаимодействие ToDo
Помнить о принципе тонкого контроллера, все перенести в сервис
*/

package org.example.tdlsb.controller;

import org.example.tdlsb.service.ToDoService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToDoRestController {
    private final ToDoService toDoService;

    public ToDoRestController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    // Создать задачу !!! Перенести создание задачи в сервис !!!

    // Найти по названию +

    // Показать активные +

    // Показать выполненные +

    // Показать все +

    /* Отметить выполненным -
    При этом, часть с получением списка, и выбором из него уходит на фронтэнд
     */

    // Изменить дедлайн (логика та же, что и с выполненным) -

    // Удалить задачу (логика та же, что и с выполненным) -


}
