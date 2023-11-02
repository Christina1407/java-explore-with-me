package ru.practicum.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {


    List<User> findByIdIn(List<Long> ids, Pageable pageable);

    boolean existsByName(String userName);

    boolean existsByEmail(String email);
}
