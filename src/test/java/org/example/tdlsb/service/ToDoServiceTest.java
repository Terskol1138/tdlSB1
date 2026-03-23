package org.example.tdlsb.service;

import org.example.tdlsb.dto.CreateToDoRequest;
import org.example.tdlsb.dto.ToDoResponse;
import org.example.tdlsb.dto.UpdateToDoRequest;
import org.example.tdlsb.entity.ToDoItem;
import org.example.tdlsb.repository.ToDoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class ToDoServiceTest {

    @Mock
    private ToDoRepository repository;

    @InjectMocks
    private ToDoService service;

    @Captor
    private ArgumentCaptor<ToDoItem> todoCaptor;

    private ToDoItem testToDo;
    private String testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID().toString();
        testToDo = new ToDoItem("Test Title", "Test Description", 5);
        testToDo.setId(testId);
        testToDo.setCreatedAt(LocalDateTime.now());
        testToDo.setDone(false);
    }

    @Test
    void createToDo_WithValidData_ShouldCreateToDo() {

        CreateToDoRequest request = CreateToDoRequest.builder()
                .title("Test Title")
                .description("Test Description")
                .daysToDo(5)
                .build();

        when(repository.save(any(ToDoItem.class))).thenReturn(testToDo);
        when(repository.findById(testId)).thenReturn(Optional.of(testToDo));

        ToDoResponse response = service.createToDo(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testId);
        assertThat(response.getTitle()).isEqualTo("Test Title");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getDaysToDo()).isEqualTo(5);
        assertThat(response.getStatus()).isEqualTo("ACTIVE");

        verify(repository, times(1)).save(any(ToDoItem.class));
        verify(repository, times(1)).findById(testId);
        verify(repository, times(1)).flush();

    }

    @Test
    void getToDoById_WithNonExistentId_ShouldThrowException() {

        String nonExistentId = "non-existent-id";
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getToDoById(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found with ID: " + nonExistentId);

        verify(repository, times(1)).findById(nonExistentId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void markAsDone_WithActiveTodo_ShouldMarkCompleted() {
        testToDo.setDone(false);
        when(repository.findById(testId)).thenReturn(Optional.of(testToDo));
        when(repository.save(any(ToDoItem.class))).thenReturn(testToDo);

        ToDoResponse response = service.markAsDone(testId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getCompletedAt()).isNotNull();

        verify(repository, times(1)).findById(testId);
        verify(repository, times(1)).save(testToDo);
    }

    @Test
    void markAsDone_WithCompletedTodo_ShouldThrowException() {
        testToDo.setDone(true);
        when(repository.findById(testId)).thenReturn(Optional.of(testToDo));

        assertThatThrownBy(() -> service.markAsDone(testId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already completed");

        verify(repository, times(1)).findById(testId);
        verify(repository, never()).save(any(ToDoItem.class));
    }

    @Test
    void updateToDo_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        UpdateToDoRequest request = UpdateToDoRequest.builder()
                .title("Updated Title")
                .daysToDo(10)
                .build();

        when(repository.findById(testId)).thenReturn(Optional.of(testToDo));
        when(repository.save(any(ToDoItem.class))).thenReturn(testToDo);

        ToDoResponse response = service.updateToDo(testId, request);

        verify(repository).save(todoCaptor.capture());
        ToDoItem updatedTodo = todoCaptor.getValue();

        assertThat(updatedTodo.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTodo.getDaysToDo()).isEqualTo(10);
        assertThat(updatedTodo.getDescription()).isEqualTo("Test Description"); // не изменилось
    }

    @Test
    void deleteToDo_WithExistingId_ShouldDeleteTodo() {
        when(repository.existsById(testId)).thenReturn(true);
        doNothing().when(repository).deleteById(testId);

        service.deleteToDo(testId);

        verify(repository, times(1)).existsById(testId);
        verify(repository, times(1)).deleteById(testId);
    }

    @Test
    void deleteToDo_WithNonExistentId_ShouldThrowException() {
        when(repository.existsById(testId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteToDo(testId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(repository, times(1)).existsById(testId);
        verify(repository, never()).deleteById(anyString());
    }



}