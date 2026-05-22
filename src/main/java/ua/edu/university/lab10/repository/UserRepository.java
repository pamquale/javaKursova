package ua.edu.university.lab10.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.university.lab10.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Поиск по email для аутентификации
    boolean existsByEmail(String email);
}