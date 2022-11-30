package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private long id;
    private String name;
    private String email;
}