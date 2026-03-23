package org.example.tdlsb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tdlsb.dto.CreateToDoRequest;
import org.example.tdlsb.dto.ToDoResponse;
import org.example.tdlsb.dto.UpdateToDoRequest;
import org.example.tdlsb.entity.ToDoItem;
import org.example.tdlsb.entity.User;
import org.example.tdlsb.exception.ResourceNotFoundException;
import org.example.tdlsb.repository.ToDoRepository;
import org.example.tdlsb.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ToDoService {

    private final ToDoRepository todoRepository;
    private final UserRepository userRepository;

    // Получение текущего пользователя из SecurityContext
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    // CREATE
    @Transactional
    public ToDoResponse createToDo(CreateToDoRequest request) {
        log.info("Creating new todo");

        User currentUser = getCurrentUser();
        ToDoItem todo = request.toEntity(currentUser);

        ToDoItem saved = todoRepository.save(todo);
        log.info("Todo created successfully with id: {}", saved.getId());

        return ToDoResponse.fromEntity(saved);
    }

    // READ - все todo текущего пользователя
    public List<ToDoResponse> getAllToDos() {
        log.debug("Fetching all todos for current user");

        User currentUser = getCurrentUser();

        return todoRepository.findByUser(currentUser).stream()
                .map(ToDoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // READ - todo по ID (только если принадлежит текущему пользователю)
    public ToDoResponse getToDoById(String id) {
        log.debug("Fetching todo with ID: {}", id);

        User currentUser = getCurrentUser();
        ToDoItem todo = findToDoOrThrow(id);

        // Проверяем, что todo принадлежит текущему пользователю
        if (!todo.getUser().getId().equals(currentUser.getId())) {
            log.warn("User {} attempted to access todo {} of another user",
                    currentUser.getUsername(), id);
            throw new ResourceNotFoundException("Todo not found with ID: " + id);
        }

        return ToDoResponse.fromEntity(todo);
    }

    // READ - активные todo текущего пользователя
    public List<ToDoResponse> getActiveToDos() {
        log.debug("Fetching active todos");

        User currentUser = getCurrentUser();

        return todoRepository.findByUserAndDoneFalseOrderByCreatedAtDesc(currentUser).stream()
                .map(ToDoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // READ - завершенные todo текущего пользователя
    public List<ToDoResponse> getCompletedToDos() {
        log.debug("Fetching completed todos");

        User currentUser = getCurrentUser();

        return todoRepository.findByUserAndDoneTrueOrderByCompletedAtDesc(currentUser).stream()
                .map(ToDoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // READ - поиск по title (только среди своих)
    public List<ToDoResponse> searchByTitle(String title) {
        log.debug("Searching by title: {}", title);

        User currentUser = getCurrentUser();

        return todoRepository.findByUserAndTitleContainingIgnoreCase(currentUser, title).stream()
                .map(ToDoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public ToDoResponse updateToDo(String id, UpdateToDoRequest request) {
        log.info("Updating todo with ID: {}", id);

        User currentUser = getCurrentUser();
        ToDoItem todo = findToDoOrThrow(id);

        // Проверяем владельца
        if (!todo.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Todo not found with ID: " + id);
        }

        request.updateEntity(todo);

        ToDoItem updated = todoRepository.save(todo);
        log.info("Todo updated successfully");

        return ToDoResponse.fromEntity(updated);
    }

    // MARK AS DONE
    @Transactional
    public ToDoResponse markAsDone(String id) {
        log.info("Marking todo as done with ID: {}", id);

        User currentUser = getCurrentUser();
        ToDoItem todo = findToDoOrThrow(id);

        // Проверяем владельца
        if (!todo.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Todo not found with ID: " + id);
        }

        if (todo.isDone()) {
            log.warn("Todo {} is already done", id);
            throw new IllegalStateException("Todo is already completed");
        }

        todo.markAsDone();
        ToDoItem updated = todoRepository.save(todo);

        log.info("Todo {} marked as done", id);
        return ToDoResponse.fromEntity(updated);
    }

    // DELETE
    @Transactional
    public void deleteToDo(String id) {
        log.info("Deleting todo with ID: {}", id);

        User currentUser = getCurrentUser();
        ToDoItem todo = findToDoOrThrow(id);

        // Проверяем владельца
        if (!todo.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Todo not found with ID: " + id);
        }

        todoRepository.deleteById(id);
        log.info("Todo deleted successfully");
    }

    @Transactional
    public void deleteAllCompleted() {
        log.info("Deleting all completed todos for current user");

        User currentUser = getCurrentUser();
        List<ToDoItem> completed = todoRepository.findByUserAndDoneTrue(currentUser);
        todoRepository.deleteAll(completed);

        log.info("Deleted {} completed todos", completed.size());
    }

    // STATISTICS
    public long getTotalCount() {
        return todoRepository.countByUser(getCurrentUser());
    }

    public long getActiveCount() {
        return todoRepository.countByUserAndDoneFalse(getCurrentUser());
    }

    public long getCompletedCount() {
        return todoRepository.countByUserAndDoneTrue(getCurrentUser());
    }

    // HELPER
    private ToDoItem findToDoOrThrow(String id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with ID: " + id));
    }
}