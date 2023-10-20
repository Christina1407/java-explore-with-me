package ru.practicum.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repo.UserRepository;


@Component
@AllArgsConstructor
public class UserManager {
    private final UserRepository userRepository;

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id =  " + userId + " was not found"));
    }
}
