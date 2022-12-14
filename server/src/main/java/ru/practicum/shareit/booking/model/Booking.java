package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
@Builder(toBuilder = true)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @FutureOrPresent
    @Column(name = "start_date")
    private LocalDateTime start;

    @Future
    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item, User booker) {
        this.start = start;
        this.end = end;
        this.status = Status.WAITING;
        this.item = item;
        this.booker = booker;
    }
}