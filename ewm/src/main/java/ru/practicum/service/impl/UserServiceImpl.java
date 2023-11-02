package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.manager.UserManager;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.model.dto.UserDto;
import ru.practicum.repo.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserManager userManager;

    @Override
    public UserDto saveUser(UserDto userDto) {
        //проверяем, что имени и почты нет в базе
        existsByName(userDto.getName());
        existsByEmail(userDto.getEmail());
        User user = userMapper.map(userDto);
        return userMapper.map(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userManager.findUserById(userId);
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        List<User> users = (isNull(ids) || ids.isEmpty()) ?
                userRepository.findAll(pageable).getContent() :
                userRepository.findByIdIn(ids, pageable);
        return userMapper.map(users);
    }

    private void existsByName(String userName) {
        if (userRepository.existsByName(userName)) {
            throw new ConflictException("Constraint unique_user_name", "Integrity constraint has been violated.");
        }
    }

    private void existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Constraint unique_user_email", "Integrity constraint has been violated.");
        }
    }
}
