package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.Date;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ItemRequest {
    private long id;
    private String description;
    private User requestor;
    private Date created;
}
