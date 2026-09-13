package com.ecommerce.repository;

import com.ecommerce.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(String role);

    @Query("""
        SELECT u FROM User u
        WHERE
        (
            :search IS NULL
            OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        AND
        (
            :role IS NULL
            OR u.role = :role
        )
        """)
    Page<User> searchUsers(
            @Param("search") String search,
            @Param("role") String role,
            Pageable pageable
    );
}