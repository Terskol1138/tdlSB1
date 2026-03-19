package org.example.tdlsb.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tdlsb.dto.CreateUserRequest;
import org.example.tdlsb.dto.UpdateUserRequest;
import org.example.tdlsb.dto.UserResponse;
import org.example.tdlsb.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
            ) {
        log.info("POST /api/users - Creating new user with username: {}", request.getUsername());

        UserResponse created =  userService.createUser(request);

        URI location = URI.create("/api/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");

        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        log.info("GET /api/users - Fetching user with id: {}", id);

        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("GET /api/users - Fetching user by username: {}", username);

        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/users - Fetching user by email: {}", email);

        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/with-todos")
    public ResponseEntity<UserResponse> getUserWithTodos(@PathVariable String id) {
        log.info("GET /api/users/{}/with-todos - Fetching user with todos", id);

        UserResponse user = userService.getUserWithToDos(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request){
        log.info("PUT /api/users/{} - Updating user with username: {}", id, request.getUsername());

        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")//Does not work!
    public ResponseEntity<Void> deleteUser(@PathVariable String id){
        log.info("DELETE /api/users/{} - Deleting user", id);

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        log.info("GET /api/users/count - Getting total user count");

        long count = userService.getTotalUserCount();
        return ResponseEntity.ok(count);
    }

}
