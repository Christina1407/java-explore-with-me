package ru.practicum.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.StatClient;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.CategoryManager;
import ru.practicum.manager.EventManager;
import ru.practicum.manager.UserManager;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.UpdateEventRequest;
import ru.practicum.model.enums.StateActionEnum;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.LocationRepository;
import ru.practicum.repo.RequestRepository;
import ru.practicum.service.EventService;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private UserManager userManager;
    @Mock
    private EventManager eventManager;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private CategoryManager categoryManager;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private StatClient statClient;
    @Mock
    private RequestMapper requestMapper;
    @Mock
    private EntityManager entityManager;
    @Captor
    ArgumentCaptor<Event> eventCaptor;
    private EventService eventService;
    private Event event;

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(userManager, eventManager, eventRepository, requestRepository,
                eventMapper, categoryManager, locationRepository, locationMapper, statClient, requestMapper, entityManager);
        event = Event.builder()
                .initiator(User.builder().id(1L).build())
                .build();
    }

    @Test
    void findInitiatorEventById() {
        //before
        when(eventManager.findEventById(eq(2L))).thenReturn(event);
        //when
        EventFullDto result = eventService.findInitiatorEventById(1L, 2L);
        //then
        assertThat(result).isNull();

        // Получение события не инициатором
        //before
        event.getInitiator().setId(2L);
        //when
        assertThatThrownBy(() -> eventService.findInitiatorEventById(1L, 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateEventByInitiator() {
        //before
        UpdateEventRequest updateEventRequest = new UpdateEventRequest();
        updateEventRequest.setStateAction(StateActionEnum.SEND_TO_REVIEW);
        when(eventManager.findEventById(eq(2L))).thenReturn(event);
        //when
        eventService.updateEventByInitiator(1L, 2L, updateEventRequest);
        verify(eventMapper, times(1)).update(any(), any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().getState()).isEqualTo(StateEnum.PENDING);
    }
}