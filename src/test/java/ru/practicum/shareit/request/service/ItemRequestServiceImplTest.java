package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;
import ru.practicum.shareit.pageable.OffsetLimitPageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    public void testAddRequest_ShouldSaveItemRequest_WhenOk() {
        User user = new User();
        user.setId(1L);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequest result = itemRequestService.addRequest(itemRequest);

        verify(userRepository).findById(user.getId());
        ArgumentCaptor<ItemRequest> captor = ArgumentCaptor.forClass(ItemRequest.class);
        verify(itemRequestRepository).save(captor.capture());
        ItemRequest savedItemRequest = captor.getValue();
        assertEquals(itemRequest, result);
        assertEquals(1L, savedItemRequest.getRequestor().getId());
        assertEquals(user, savedItemRequest.getRequestor());
    }

    @Test
    public void testAddRequest_ShouldThrowObjectNotFoundException_WhenUserDoesNotExist() {
        ItemRequest itemRequest = new ItemRequest();
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        itemRequest.setRequestor(user);
        when(userRepository.findById(itemRequest.getRequestor().getId())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.addRequest(itemRequest));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetItemRequestsByUserSorted_ReturnsSortedItemRequestDtos_UserIdExists() {
        long userId = 1L;
        long userId2 = 2L;
        User user = new User();
        user.setId(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setDescription("ghgh");
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(user);
        itemRequest2.setDescription("fhfh");
        List<ItemRequest> itemRequests = Arrays.asList(
                itemRequest, itemRequest2
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)).thenReturn(itemRequests);

        List<ItemRequestDto> result = itemRequestService.getItemRequestsByUserSorted(userId);

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findAllByRequestorIdOrderByCreatedDesc(userId);
        verify(itemRequestRepository, times(1)).findAllByRequestorIdOrderByCreatedDesc(userId);
        assertEquals(itemRequests.size(), result.size());
        assertEquals(itemRequests.get(0).getRequestor().getId(), result.get(0).getRequestId());
        assertEquals(itemRequests.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(itemRequests.get(1).getDescription(), result.get(1).getDescription());
    }

    @Test
    public void testGetItemRequestsByUserSorted_ThrowsObjectNotFoundException_WhenUserIdDoesNotExist() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequestsByUserSorted(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetItemRequestsSorted_ShouldReturnSortedItemRequestDtos() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        User requestor = new User();
        requestor.setId(userId);
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("767");
        itemRequest1.setRequestor(requestor);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("546546");
        itemRequest2.setRequestor(requestor);
        List<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);
        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.getItemRequestsSorted(
                userId, new OffsetLimitPageable(from, size))).thenReturn(itemRequests
        );

        List<ItemRequestDto> result = itemRequestService.getItemRequestsSorted(userId, from, size);

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(
                1)).getItemRequestsSorted(userId, new OffsetLimitPageable(from, size)
        );
        assertNotNull(result);
        assertEquals(itemRequests.size(), result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(1L, result.get(0).getRequestId());
    }

    @Test
    public void testGetItemRequestsSorted_WhenUserNotFound_ShouldThrowObjectNotFoundException() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequestsSorted(userId, from, size));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetItemRequestById_ShouldReturnItemRequestDto_WhenOk() {
        long userId = 1L;
        long requestId = 2L;
        User requestor = new User();
        requestor.setId(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setRequestor(requestor);
        itemRequest.setDescription("gghgh");
        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.getItemRequestById(userId, requestId);

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("gghgh", result.getDescription());
    }

    @Test
    public void testGetItemRequestById_WhenItemRequestNotFound_ShouldThrowObjectNotFoundException() {
        long userId = 1L;
        long requestId = 2L;
        User requestor = new User();
        requestor.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequestById(userId, requestId));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
    }
}