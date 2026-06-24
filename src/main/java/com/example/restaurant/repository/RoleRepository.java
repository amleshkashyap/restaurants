package com.example.restaurant.repository;

import com.example.restaurant.model.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    @Query(
            value = "SELECT COUNT(*) FROM roles WHERE name = :name",
            nativeQuery = true
    )
    public int countRolesByName(@Param("name") String name);
    public Optional<Role> findByName(String name);
}