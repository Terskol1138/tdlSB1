package org.example.tdlsb.repository;

import org.example.tdlsb.entity.ToDoItem;
import org.example.tdlsb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDoItem, String> {

    // Методы для конкретного пользователя
    List<ToDoItem> findByUser(User user);
    List<ToDoItem> findByUserAndTitleContainingIgnoreCase(User user, String title);
    List<ToDoItem> findByUserAndDoneFalseOrderByCreatedAtDesc(User user);
    List<ToDoItem> findByUserAndDoneTrueOrderByCompletedAtDesc(User user);
    List<ToDoItem> findByUserAndDoneTrue(User user);

    // Счетчики для конкретного пользователя
    long countByUser(User user);
    long countByUserAndDoneFalse(User user);
    long countByUserAndDoneTrue(User user);
}