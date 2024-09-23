package com.bookstoreapp.repository.user;

import com.bookstoreapp.model.Role;
import com.bookstoreapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("SELECT u.roles FROM User u WHERE u.email = :email")
    Set<Role> findRolesByUsername(@Param("email") String email);
}
