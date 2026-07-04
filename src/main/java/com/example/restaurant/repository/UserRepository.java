package com.example.restaurant.repository;

import com.example.restaurant.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findOneWithRolesByEmailIgnoreCase(String email);
    User getReferenceById(Long id);
}
