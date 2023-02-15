package com.backend.bankingapp.repositories;

import com.backend.bankingapp.models.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The RoleRepository interface extends JpaRepository to allow for CRUD operations
 * on Role entities in the database.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
