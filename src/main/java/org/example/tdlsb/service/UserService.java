package org.example.tdlsb.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tdlsb.dto.CreateUserRequest;
import org.example.tdlsb.dto.UpdateUserRequest;
import org.example.tdlsb.dto.UserResponse;
import org.example.tdlsb.entity.User;
import org.example.tdlsb.exception.BusinessRuleException;
import org.example.tdlsb.exception.ResourceNotFoundException;
import org.example.tdlsb.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("User with username {} already exists", request.getUsername());
            throw new BusinessRuleException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword
        );

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}",  savedUser.getId());

        return UserResponse.fromEntity(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");

        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        log.debug("Fetching user by ID: {}", id);

        User user = findUserById(id);
        return UserResponse.fromEntity(user);

    }

    public UserResponse getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + username));

        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + email));

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = findUserById(id);

        if (request.getUsername() != null) {
            if (!request.getUsername().equals(user.getUsername()) &&
                    userRepository.existsByUsername(request.getUsername())) {
                throw new BusinessRuleException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessRuleException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            user.setPasswordHash(request.getPassword());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", id);

        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(String id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found" + id);

        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);

    }

    public long getTotalUserCount() {
        return userRepository.count();
    }

    public UserResponse getUserWithToDos(String id) {
        log.debug("Fetching user with todos");

        User user = userRepository.findByIdWithToDos(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + id));

        return UserResponse.fromEntity(user);
    }



    private User findUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User is not found with id: " + id));
    }
}
