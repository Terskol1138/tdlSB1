//Интерфейс взаимодействия с данными
package org.example.tdlsb.repository;
import org.example.tdlsb.entity.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ToDoRepository extends JpaRepository<ToDoItem, String> {
    List<ToDoItem> findByTitleContainingIgnoreCase(String title);
    List<ToDoItem> findByDone(boolean done);
    List<ToDoItem> findByDoneFalseOrderByCreatedAtDesc();
    List<ToDoItem> findByDoneTrueOrderByCompletedAtDesc();
    boolean existsByTitle(String title);

    @Query("SELECT t FROM ToDoItem t WHERE t.done = false AND t.daysToDo <= :days")
    List<ToDoItem> findUrgentTodos(@Param("days") int days);
}
