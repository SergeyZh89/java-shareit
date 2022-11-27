package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT i from Item i " +
            "where (LOWER(i.name) like CONCAT('%',LOWER(?1),'%') " +
            "OR LOWER(i.description) like CONCAT('%',LOWER(?1),'%'))" +
            "AND i.available = true")
    List<Item> searchAllByText(String text);

    List<Item> findAllByRequest_Id(long requestId);
}