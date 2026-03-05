package org.example.tdlsb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tdlsb.dto.CreateToDoRequest;
import org.example.tdlsb.dto.ToDoResponse;
import org.example.tdlsb.dto.UpdateToDoRequest;
import org.example.tdlsb.entity.ToDoItem;
import org.example.tdlsb.repository.ToDoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ToDoService {

    private final ToDoRepository repository;

    //create
    @Transactional
    public ToDoResponse createToDo(CreateToDoRequest request) {
        ToDoItem todo = request.toEntity();
        ToDoItem saved = repository.save(todo);

        // Принудительно выполняем SQL и загружаем свежий объект
        repository.flush();
        ToDoItem fresh = repository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Just saved, but not found?"));

        return ToDoResponse.fromEntity(fresh);
    }
    //read
    public List<ToDoResponse> getAllToDos() {
        log.debug("Fetching all to dos");

        return repository.findAll()
                .stream()
                .map(ToDoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ToDoResponse getToDoById(String id) {
        log.debug("Fletching todo with ID: {}", id);

        ToDoItem todo = findToDoOrThrow(id);
        return ToDoResponse.fromEntity(todo);
    }

    public List<ToDoResponse> getActiveToDos() {
        log.debug("Fetching active ToDos");

        return repository.findByDoneFalseOrderByCreatedAtDesc()
                .stream()
                .map(ToDoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ToDoResponse> getCompletedToDos() {
        log.debug("Fetching completed ToDos");

        return repository.findByDoneTrueOrderByCompletedAtDesc()
                .stream()
                .map(ToDoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ToDoResponse> searchByTitle(String title) {
        log.debug("Searching by title: {}", title);

        return repository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(ToDoResponse::fromEntity)
                .collect(Collectors.toList());
    }


    //update
    @Transactional
    public ToDoResponse updateToDo(String id, UpdateToDoRequest request) {
        log.info("Updating todo with ID: {}", id);

        ToDoItem todo = findToDoOrThrow(id);
        request.updateEntity(todo);

        ToDoItem updated = repository.save(todo); //null?
        log.info("ToDo updated successfully");
        return ToDoResponse.fromEntity(updated);
    }

    @Transactional
    public ToDoResponse markAsDone(String id) {
        log.info("Marking ToDo as done with ID: {}", id);

        ToDoItem todo = findToDoOrThrow(id);
        if (todo.isDone()) {
            log.warn("ToDo {} is already done", id);
            throw new IllegalStateException("ToDo is already completed");
        }

        todo.markAsDone();
        ToDoItem updated = repository.save(todo);

        log.info("ToDo {} marked as done", id);
        return ToDoResponse.fromEntity(updated);
    }

    //delete
    @Transactional
    public void deleteToDo(String id) {
        log.info("Deleting todo with ID: " + id); //почему не "...with ID: {}", id); ?

        if (!repository.existsById(id)) {
            throw new RuntimeException("Todo with ID: " + id + " not found");
        }
        repository.deleteById(id);
        log.info("ToDo {} deleted successfully", id);

    }

    @Transactional
    public void deleteAllCompleted() {

        log.info("Deleting all completed todos");
        List<ToDoItem> completed = repository.findByDoneTrueOrderByCompletedAtDesc(); //У меня в репозитории не прописан этот метод(как у тебя), подразумевался метод Spring?
        repository.deleteAll(completed);
        log.info("Deleted all completed todos");
    }

    //helper
    private ToDoItem findToDoOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("ToDo not found with ID: {}", id); // почему ..., id);
                    return new RuntimeException("ToDo not found with ID: " + id); //а здесь ...+ id);
                }); //Я недавно слушал, что в кастомных Exception наследоваться от Error - грубая ошибка. Так понимаю, здесь error связан с системой log?
    }

    //statistics
    public long getTotalCount() {
        return repository.count();
    }

    public long getActiveCount() {
        return repository.findByDoneFalseOrderByCreatedAtDesc().size(); //у тебя тут тоже findByDoneFalse()...
    }

    public long getCompletedCount() {
        return repository.findByDoneTrueOrderByCompletedAtDesc().size();
    }
}
