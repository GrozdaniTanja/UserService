package com.tanja.user_service;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.tanja.user_service.model.User;
import com.tanja.user_service.repository.UserRepository;
@QuarkusTest
public class UserServiceTest {

    @Inject
    UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");
        user.setPassword("password");
        user.setRole("user");
        userRepository.persist(user);
    }

    @Test
    void testGetUserById() {
        User find_user = userRepository.findById(user.getId());
        assertEquals("John", find_user.getFirstName());
        assertEquals("Doe", find_user.getLastName());
        assertEquals("johndoe@example.com", find_user.getEmail());
        assertEquals("password", find_user.getPassword());
        assertEquals("user", find_user.getRole());
    }
}