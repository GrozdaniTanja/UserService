package com.tanja.user_service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class UserServiceTest {

    private ManagedChannel channel;
    private UserServiceGrpc.UserServiceBlockingStub blockingStub;

    @BeforeEach
    public void setup() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9000)
                .usePlaintext()
                .build();
        blockingStub = UserServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void tearDown() {
        channel.shutdownNow();
    }

    @Test
    public void testCreateUser() {
        User.CreateUserRequest request = User.CreateUserRequest.newBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john.doe@example.com")
                .setPassword("password")
                .setRole("user")
                .build();


        User.UserResponse response = blockingStub.createUser(request);

        assertNotNull(response);
        assertNotNull(response.getId());
    }
}
