package org.example.tdlsb.controller;

import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.tdlsb.dto.CreateToDoRequest;
import org.example.tdlsb.dto.ToDoResponse;
import org.example.tdlsb.dto.UpdateToDoRequest;
import org.example.tdlsb.service.ToDoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService toDoService;

    //create
    @PostMapping
    public ResponseEntity<ToDoResponse> createToDo(@Valid @RequestBody CreateToDoRequest request) {
        log.info("POST /api/todos - Creating new todo with title: {}", request.getTitle());

        ToDoResponse created = toDoService.createToDo(request);

        URI location = URI.create("/api/todos/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    //read
    @GetMapping
    public ResponseEntity<List<ToDoResponse>> getAllToDo() {
        log.info("GET /api/todos - Fetching all todos");

        List<ToDoResponse> todos = toDoService.getAllToDos();
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToDoResponse> getToDoById(@PathVariable String id) {
        log.info("GET /api/todos/{} - Fetching todo by ID", id);

        ToDoResponse todo =toDoService.getToDoById(id);

        return ResponseEntity.ok(todo);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ToDoResponse>> getActiveToDos() {
        log.info("GET /api/todos - Fetching active todos");

        List<ToDoResponse> todos = toDoService.getActiveToDos();
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<ToDoResponse>> getCompletedToDos() {
        log.info("GET /api/todos - Fetching completed todos");

        List<ToDoResponse> todos = toDoService.getCompletedToDos();
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ToDoResponse>> searchToDos(@RequestParam String title) {
        log.info("GET /api/todos - Searching for todos by title: {}", title);

        List<ToDoResponse> todos = toDoService.searchByTitle(title);
        return ResponseEntity.ok(todos);
    }

    //update
    @PutMapping("/{id}")
    public ResponseEntity<ToDoResponse> updateToDo(
            @PathVariable String id,
            @Valid @RequestBody UpdateToDoRequest request) {
        log.info("PUT /api/todos/{} - Updating todo", id);
        ToDoResponse updated = toDoService.updateToDo(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ToDoResponse> markAsDone(@PathVariable String id) {
        log.info("PATCH /api/todos/{}/complete - Marking todo as done", id);

        ToDoResponse updated = toDoService.markAsDone(id);
        return ResponseEntity.ok(updated);
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteToDo(@PathVariable String id) {
        log.info("DELETE /api/todos/{}", id);

        toDoService.deleteToDo(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/completed")
    public ResponseEntity<Void> deleteAllCompleted() {
        log.info("DELETE /api/todos/completed - Deleting all completed todos");

        toDoService.deleteAllCompleted();
        return ResponseEntity.noContent().build();
    }

    //statistics
    @GetMapping("/count")
    public ResponseEntity<ToDoCountResponse> getCounts() {
        log.info("GET /api/todos/count - getting statistics");

        ToDoCountResponse counts = ToDoCountResponse.builder()
                .total(toDoService.getTotalCount())
                .active(toDoService.getActiveCount())
                .completed(toDoService.getCompletedCount())
                .build();

        return ResponseEntity.ok(counts);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToDoCountResponse {
        private long total;
        private long active;
        private long completed;
    }
}