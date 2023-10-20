package ru.practicum.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "email is empty")
    @Email(message = "email is not well-formed email address")
    @Size(min = 6, max = 254, message = "email minLength = 6 maxLength = 254")
    private String email;
    @NotBlank(message = "name is empty")
    @Size(min = 2, max = 250, message = "name minLength = 2 maxLength = 250")
    private String name;
}
