package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor_Id(long userId);

    @Query(nativeQuery = true, value = "SELECT * FROM requests join items i on requests.id = i.request_id where owner_id = :ownerId")
    List<ItemRequest> findAllByOwnerId(long ownerId, Pageable pageable);
}