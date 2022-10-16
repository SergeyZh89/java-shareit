package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Booking {
    private long id;
    private Date start;
    private Date end;
    private Item item;
    private User booker;
}
