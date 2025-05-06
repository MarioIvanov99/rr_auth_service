package com.mario.authservice.data.repository;

import com.mario.authservice.data.entity.User;
import com.mario.authservice.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT new com.mario.authservice.dto.UserDto(u.id, u.username, u.email, u.role) " +
            "FROM User u WHERE u.id = :id AND u.isDeleted = false")
    Optional<UserDto> findUserDtoById(@Param("id") Long id);

    @Query("SELECT new com.mario.authservice.dto.UserDto(u.id, u.username, u.email, u.role) " +
            "FROM User u WHERE u.username = :username AND u.isDeleted = false")
    Optional<UserDto> findUserDtoByUsername(@Param("username") String username);

    @Query("SELECT new com.mario.authservice.dto.UserDto(u.id, u.username, u.email, u.role) " +
            "FROM User u WHERE u.isDeleted = false")
    List<UserDto> findAllActiveUsers();

    @Modifying
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.id = :id")
    void softDeleteUserById(@Param("id") Long id);
}
