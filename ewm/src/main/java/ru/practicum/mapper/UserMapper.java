package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.User;
import ru.practicum.model.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto map(User user);

    User map(UserDto userDto);

    List<UserDto> map(List<User> users);
}
