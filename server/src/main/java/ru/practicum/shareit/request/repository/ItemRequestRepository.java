package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Boolean existsByDescription(String description);

    List<ItemRequest> findAllByOwnerId(Long userId, Sort sort);

    List<ItemRequest> findByOwnerIdNot(Long userId, Sort sort);
}
