package com.backend.bankingapp.repositories;

import com.backend.bankingapp.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The UserRepository interface extends JpaRepository to allow for CRUD operations
 * on User entities in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Method to find a User entity by its username field
     *
     * @param username The username of the User entity to search for
     * @return The found User entity or null if not found
     */
    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long id);
}
