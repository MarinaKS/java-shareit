package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.dto.BookingForItemResponseDto;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
public class ItemResponseWithBookingDto {
    private Long id;
    @NonNull
    @NotEmpty
    private String name;
    @NonNull
    @NotEmpty
    private String description;
    @NonNull
    private Boolean available;
    private Long request;
    private BookingForItemResponseDto lastBooking;
    private BookingForItemResponseDto nextBooking;
    private List<CommentDto> comments;
}
