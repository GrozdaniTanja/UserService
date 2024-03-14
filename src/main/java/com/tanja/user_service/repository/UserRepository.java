package com.tanja.user_service.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import com.tanja.user_service.model.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User>{

}
