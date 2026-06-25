package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.enums.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.constants.SqlConstants.CURRENT_TIMESTAMP;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start <= " + CURRENT_TIMESTAMP + " AND " +
            "b.end >= " + CURRENT_TIMESTAMP + " ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < " + CURRENT_TIMESTAMP + " " +
            "ORDER BY b.end DESC")
    List<Booking> findPastByBookerId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > " + CURRENT_TIMESTAMP + " " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.start <= " + CURRENT_TIMESTAMP + " AND " +
            "b.end >= " + CURRENT_TIMESTAMP + " ORDER BY b.start DESC")
    List<Booking> findCurrentByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.end < " + CURRENT_TIMESTAMP + " " +
            "ORDER BY b.end DESC")
    List<Booking> findPastByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.start > " + CURRENT_TIMESTAMP + " " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.status = :bookingStatus ORDER BY b.start DESC")
    List<Booking> findByItemIdsAndStatus(@Param("itemIds") List<Long> itemIds,
                                         @Param("bookingStatus") BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds ORDER BY b.start DESC")
    List<Booking> findAllByItemIds(@Param("itemIds") List<Long> itemIds);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :id AND b.end < :now ORDER BY b.end DESC")
    List<Booking> findPastBookingsByItemId(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :id AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findFutureByItemId(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.booker.id = :userId AND " +
            "b.item.id = :itemId AND " +
            "b.status = 'APPROVED' AND " +
            "b.end < :now")
    List<Booking> findByBookerIdAndItemIdAndEndDateBefore(@Param("userId") Long userId,
                                                          @Param("itemId") Long itemId,
                                                          @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.booker.id = :userId AND " +
            "b.item.id = :itemId AND " +
            "b.status = 'APPROVED'")
    List<Booking> findByBookerIdAndItemIdAndStatus(@Param("userId") Long userId,
                                                   @Param("itemId") Long itemId);
}
