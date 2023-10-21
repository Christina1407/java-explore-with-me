package ru.practicum.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.User;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {


    List<User> findByIdIn(List<Long> ids, Pageable pageable);

    boolean existsByName(String userName);

    boolean existsByEmail(String email);
}
