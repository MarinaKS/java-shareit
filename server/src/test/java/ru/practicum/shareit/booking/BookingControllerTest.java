package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Captor
    private ArgumentCaptor<Long> bookingIdCaptor;

    @Captor
    private ArgumentCaptor<Boolean> approvedCaptor;

    @Test
    public void addBookingTest() throws Exception {
        // Raw JSON for request
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);

        String bookingJson = "{" +
                "\"start\": \"" + startTime + "\"," +
                "\"end\": \"" + endTime + "\"," +
                "\"itemId\": 100," +
                "\"status\": \"WAITING\"" +
                "}";

        // Create a Booking to return from our mock service method
        Booking mockedBooking = new Booking();
        mockedBooking.setId(2L);
        mockedBooking.setStart(startTime);
        mockedBooking.setEnd(endTime);
        Item item = new Item();
        item.setId(100L);
        mockedBooking.setItem(item);
        User user = new User();
        user.setId(200L);
        mockedBooking.setBooker(user);
        mockedBooking.setStatus(Status.WAITING);

        // Mock the service method
        when(bookingService.addBooking(bookingCaptor.capture())).thenReturn(mockedBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.item.id").value(100L))
                .andExpect(jsonPath("$.booker.id").value(200L))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists());

        // Assert the properties of the captured Booking object
        Booking capturedBooking = bookingCaptor.getValue();
        assertNull(capturedBooking.getId());
        assertEquals(100L, capturedBooking.getItem().getId());
        assertEquals(200L, capturedBooking.getBooker().getId());
        assertEquals(Status.WAITING, capturedBooking.getStatus());
    }

    @Test
    public void approveStatusTest() throws Exception {
        long userId = 200L;
        long bookingId = 2L;
        boolean approved = true;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        Booking mockedBooking = new Booking();
        mockedBooking.setId(bookingId);
        mockedBooking.setStart(startTime);
        mockedBooking.setEnd(endTime);
        Item item = new Item();
        item.setId(100L);
        mockedBooking.setItem(item);
        User user = new User();
        user.setId(userId);
        mockedBooking.setBooker(user);
        mockedBooking.setStatus(approved ? Status.APPROVED : Status.WAITING);
        when(bookingService.approveStatus(eq(userId), eq(bookingId), eq(approved))).thenReturn(mockedBooking);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(100L))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value(approved ? "APPROVED" : "WAITING"))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists());
    }

    @Test
    public void getBookingTest() throws Exception {
        long userId = 200L;
        long bookingId = 2L;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        Booking mockedBooking = new Booking();
        mockedBooking.setId(bookingId);
        mockedBooking.setStart(startTime);
        mockedBooking.setEnd(endTime);
        Item item = new Item();
        item.setId(100L);
        mockedBooking.setItem(item);
        User user = new User();
        user.setId(userId);
        mockedBooking.setBooker(user);
        mockedBooking.setStatus(Status.WAITING);
        when(bookingService.getBooking(userId, bookingId)).thenReturn(mockedBooking);

        mockMvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.start").value(startsWith(startTime.withNano(0).toString())))
                .andExpect(jsonPath("$.end").value(startsWith(endTime.withNano(0).toString())))
                .andExpect(jsonPath("$.item.id").value(100L))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).getBooking(userId, bookingId);
    }

    @Test
    public void getBookingsByUserIdSortedTest() throws Exception {
        long userId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 5;
        List<Booking> bookingsList = new ArrayList<>();
        for (long i = 1; i <= size; i++) {
            Booking booking = new Booking();
            booking.setId(i);
            booking.setStart(LocalDateTime.now().plusHours(i));
            booking.setEnd(LocalDateTime.now().plusHours(i + 1));
            booking.setStatus(Status.WAITING);//статус будет разный
            Item item = new Item();
            item.setId(i + 10);
            item.setName("name-" + (i + 10));
            booking.setItem(item);
            User booker = new User();
            booker.setId(i + 20);
            booker.setName("name-" + (i + 20));
            booking.setBooker(booker);
            bookingsList.add(booking);
        }
        when(bookingService.getBookingsByUserIdSorted(userId, state, from, size)).thenReturn(bookingsList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("state", state.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(size))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[4].id").value(5L));

        verify(bookingService).getBookingsByUserIdSorted(userId, state, from, size);
    }

    @Test
    public void getBookingsByItemsTest() throws Exception {
        long userId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 5;
        List<Booking> bookingList = new ArrayList<>();
        for (long i = 1; i <= size; i++) {
            Booking booking = new Booking();
            booking.setId(i);
            booking.setStart(LocalDateTime.now().plusDays(i));
            booking.setEnd(LocalDateTime.now().plusDays(i).plusHours(4));
            Item item = new Item();
            item.setId(100L + i);
            booking.setItem(item);
            User booker = new User();
            booker.setId(200L + i);
            booking.setBooker(booker);
            booking.setStatus(Status.WAITING);
            bookingList.add(booking);
        }
        List<BookingResponseDto> bookingDtos = bookingList.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
        when(bookingService.getBookingsByItems(userId, state, from, size)).thenReturn(bookingList);

        ResultActions result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());
        result.andExpect(jsonPath("$.length()").value(bookingDtos.size()));
        for (int i = 0; i < size; i++) {
            String bookingBasePath = String.format("$[%d]", i);
            result.andExpect(jsonPath(bookingBasePath + ".id").value(bookingDtos.get(i).getId()))
                    .andExpect(jsonPath(bookingBasePath + ".item.id").value(bookingDtos.get(i).getItem().getId()))
                    .andExpect(jsonPath(bookingBasePath + ".booker.id").value(bookingDtos.get(i).getBooker().getId()))
                    .andExpect(jsonPath(bookingBasePath + ".status").value(bookingDtos.get(i).getStatus().toString()))
                    .andExpect(jsonPath(bookingBasePath + ".start").exists())
                    .andExpect(jsonPath(bookingBasePath + ".end").exists());
        }
        verify(bookingService).getBookingsByItems(userId, state, from, size);
    }

}