package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.CategoryManager;
import ru.practicum.manager.EventManager;
import ru.practicum.manager.UserManager;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.*;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.model.dto.NewEventDto;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.LocationRepository;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventServiceImpl implements EventService {
    private final UserManager userManager;
    private final EventManager eventManager;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryManager categoryManager;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final StatClient statClient;

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        User user = userManager.findUserById(userId);
        Category category = categoryManager.findCategoryById(newEventDto.getCategory());
        Location location = locationRepository.save(locationMapper.map(newEventDto.getLocation()));
        Event eventForSave = eventMapper.map(newEventDto, category, location, user, StateEnum.PENDING);
        Event event = eventRepository.save(eventForSave);
        return eventMapper.map(event, 0L);//TODO check mapper
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto findMyEventById(Long userId, Long eventId, String requestURI) {
        Event event = eventManager.findEventById(eventId);
        checkInitiator(userId, event);
        //TODO
        //Если событие опубликовано, то нужно добавить просмотры и запросы

        if (Objects.equals(event.getState(), StateEnum.PUBLISHED)) {
      //      views = statClient.getStats(event.getPublishedOn(), LocalDateTime.now(), new ArrayList<>(List.of(requestURI)))
        }
        return eventMapper.map(event, views);
    }

    @Override
    public EventFullDto publicFindEventById(Long eventId, String ip, String requestURI) {
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        Event event = eventManager.findEventById(eventId);
        statClient.saveHit(requestURI, "ewm", ip, LocalDateTime.now());
        //TODO
        //событие должно быть PUBLISHED
        return null;
    }

    @Override
    public List<EventShortDto> getEvents(GetEventsRequestParams params, Pageable pageable) {
        return null; //TODO
    }

    private void checkInitiator(Long userId, Event event) {
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            log.info("Пользователь c id = {} хочет получить данные события id = {}. " +
                    "Данные о событии по этому эндпоинту доступны только инициатору.", userId, event.getId());
            throw new NotFoundException(String.format("Пользователь c id = %d не инициатор события id = %d.", userId, event.getId()));
        }
    }


}
