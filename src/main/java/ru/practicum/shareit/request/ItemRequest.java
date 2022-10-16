package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    private long id;
    private String description;
    private User requestor;
    private Date created;
}