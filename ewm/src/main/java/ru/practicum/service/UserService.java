package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.model.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);
}
