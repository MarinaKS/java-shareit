package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemResponseDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.CommentValidationException;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.exceptions.UpdateItemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemResponseWithBookingDto> getItems(long userId) {
        List<Item> items = itemRepository.findAllByOwnerIdIsOrderByIdAsc(userId);
        List<ItemResponseWithBookingDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            BookingForItemResponseDto lastBooking = BookingMapper.toBookingForItemResponseDto(
                    bookingRepository.findLastBooking(
                            item.getId(), LocalDateTime.now(), PageRequest.of(0, 1)).stream().findFirst().orElse(null)
            );
            BookingForItemResponseDto nextBooking = BookingMapper.toBookingForItemResponseDto(
                    bookingRepository.findNextBooking(
                            item.getId(), LocalDateTime.now(), PageRequest.of(0, 1)).stream().findFirst().orElse(null)
            );
            itemDtos.add(ItemMapper.toItemResponseWithBookingDto(item, lastBooking, nextBooking, null));
        }
        List<Long> itemIds = new ArrayList<>();
        for (Item item : items) {
            itemIds.add(item.getId());
        }
        List<Comment> comments = commentRepository.findAllByItemIds(itemIds);
        Map<Long, List<CommentDto>> comentsMap = new HashMap<>();
        for (Comment comment : comments) {
            if (comentsMap.get(comment.getItem().getId()) == null) {
                comentsMap.put(comment.getItem().getId(), new ArrayList<>());
            }
            comentsMap.get(comment.getItem().getId()).add(CommentMapper.toCommentDto(comment));
        }
        for (ItemResponseWithBookingDto item : itemDtos) {
            item.setComments(comentsMap.get(item.getId()));
        }
        return itemDtos;
    }

    @Override
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item updatedItem) {
        validateUserIdExist(updatedItem.getOwnerId());
        Item item = itemRepository.findById(updatedItem.getId()).
                orElseThrow(() -> new ObjectNotFoundException("Нет итема с таким id"));
        validateOwner(item, updatedItem.getOwnerId());
        if (updatedItem.getIsAvailable() == null) {
            updatedItem.setIsAvailable(item.getIsAvailable());
        }
        if (updatedItem.getDescription() == null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (updatedItem.getName() == null) {
            updatedItem.setName(item.getName());
        }
        return itemRepository.save(updatedItem);
    }

    @Override
    public ItemResponseWithBookingDto getItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Нет итема с таким id"));
        BookingForItemResponseDto lastBooking;
        BookingForItemResponseDto nextBooking;
        if (item.getOwnerId() != userId) {
            lastBooking = null;
            nextBooking = null;
        } else {
            lastBooking = BookingMapper.toBookingForItemResponseDto(
                    bookingRepository.findLastBooking(
                            item.getId(), LocalDateTime.now(), PageRequest.of(0, 1)).stream().findFirst().orElse(null)
            );
            nextBooking = BookingMapper.toBookingForItemResponseDto(
                    bookingRepository.findNextBooking(
                            item.getId(), LocalDateTime.now(), PageRequest.of(0, 1)).stream().findFirst().orElse(null)
            );
        }
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = new ArrayList<>();
        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                commentDtos.add(CommentMapper.toCommentDto(comment));
            }
        }
        return ItemMapper.toItemResponseWithBookingDto(item, lastBooking, nextBooking, commentDtos);
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByNameOrDescriptionContainingIgnoreCase(text);
    }

    @Override
    public Comment addComment(Comment comment) {
        List<Booking> bookings = bookingRepository.findAllByItemIdForComment(
                comment.getItem().getId(), comment.getAuthor().getId(), LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new CommentValidationException("Этот пользователь не брал эту вещь в аренду");
        }
        User author = userRepository.findById(comment.getAuthor().getId()).
                orElseThrow(() -> new ObjectNotFoundException("Такого пользователя не существует"));
        comment.setAuthor(author);
        return commentRepository.save(comment);
    }

    boolean validateOwner(Item item, long userId) {
        if (item.getOwnerId() != userId) {
            throw new UpdateItemException("Редактировать вещь может только ее пользователь");
        }
        return true;
    }

    boolean validateUserIdExist(long userId) {
        for (User user : userRepository.findAll()) {
            if (user.getId() == userId) {
                return true;
            }
        }
        throw new ObjectNotFoundException("Такого пользователя не добавлено");
    }
}
