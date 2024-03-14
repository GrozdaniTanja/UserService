package com.tanja.user_service.service;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import jakarta.inject.Inject;
import com.tanja.user_service.User;
import com.tanja.user_service.UserServiceGrpc;
import com.tanja.user_service.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserRepository userRepository;


    @Override
    public void createUser(User.CreateUserRequest request, StreamObserver<User.UserResponse> responseStreamObserver) {
        try {
            LOG.info("Received createUser request: {}", request);
            com.tanja.user_service.model.User user = new com.tanja.user_service.model.User(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole()
            );

            ObjectId id = new ObjectId();
            user.setId(id);

            userRepository.persist(user);

            User.UserResponse response = User.UserResponse.newBuilder()
                    .setId(user.getId().toHexString())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setPassword(user.getPassword())
                    .setRole(user.getRole())
                    .build();
            // Send the response to the client
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();

            LOG.info("User created successfully with ID: {}", user.getId().toHexString());
        } catch (Exception e) {
            LOG.error("Error occurred while processing createUser request: {}", e.getMessage());
            responseStreamObserver.onError(e);
        }
    }

    @Override
    public void getUser(User.GetUserRequest request, StreamObserver<User.UserResponse> responseStreamObserver) {
        try {
            LOG.info("Received getUser request for ID: {}", request.getId());
            ObjectId id = new ObjectId(request.getId());
            com.tanja.user_service.model.User user = userRepository.findById(id);

            if (user != null) {
                User.UserResponse response = User.UserResponse.newBuilder()
                        .setId(user.getId().toHexString())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setPassword(user.getPassword())
                        .setRole(user.getRole())
                        .build();

                responseStreamObserver.onNext(response);
                responseStreamObserver.onCompleted();
                LOG.info("getUser request processed successfully for ID: {}", request.getId());
            } else {
                String errorMessage = "User not found for ID: " + request.getId();
                LOG.warn(errorMessage);
                responseStreamObserver.onError(new Exception(errorMessage));
            }
        } catch (Exception e) {
            String errorMessage = "Error occurred while processing getUser request: " + e.getMessage();
            LOG.error(errorMessage, e);
            responseStreamObserver.onError(e);
        }
    }

    @Override
    public void editUser(User.EditUserRequest request, StreamObserver<User.UserResponse> responseStreamObserver) {
        LOG.info("Received editUser request for ID: {}, modified fields: {}", request.getId());
        ObjectId id = new ObjectId(request.getId());
        com.tanja.user_service.model.User user = userRepository.findById(id);

        if (user != null) {
            if (request.hasFirstName()) {
                user.setFirstName(request.getFirstName());
            }
            if (request.hasLastName()) {
                user.setLastName(request.getLastName());
            }
            if (request.hasEmail()) {
                user.setEmail(request.getEmail());
            }
            if (request.hasPassword()) {
                user.setPassword(request.getPassword());
            }
            if (request.hasRole()) {
                user.setRole(request.getRole());
            }

            userRepository.update(user);

            User.UserResponse response = User.UserResponse.newBuilder()
                    .setId(user.getId().toHexString())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setEmail(user.getEmail())
                    .setPassword(user.getPassword())
                    .setRole(user.getRole())
                    .build();

            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
        } else {
            LOG.warn("User not found for ID: {}", request.getId());
            responseStreamObserver.onError(new Exception("User not found"));
        }
        LOG.info("editUser request processed");
    }


    @Override
    public void deleteUser(User.DeleteUserRequest request, StreamObserver<User.DeleteUserResponse> responseStreamObserver) {
        try {
            LOG.info("Received deleteUser request for ID: {}", request.getId());

            ObjectId id = new ObjectId(request.getId());
            boolean success = userRepository.deleteById(id);

            User.DeleteUserResponse response = User.DeleteUserResponse.newBuilder()
                    .setSuccess(success)
                    .build();

            LOG.info("deleteUser request processed successfully for ID: {}", request.getId());
        } catch (Exception e) {
            String errorMessage = "Error occurred while processing deleteUser request: " + e.getMessage();
            LOG.error(errorMessage, e);
            responseStreamObserver.onError(e);
        }
    }
}