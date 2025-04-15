package me.radek203.authservice.repository;

import me.radek203.authservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository interface for accessing user data in the database.
 * It extends JpaRepository to provide CRUD operations and custom query methods.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
