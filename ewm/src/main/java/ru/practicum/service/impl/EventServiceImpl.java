package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.manager.CategoryManager;
import ru.practicum.manager.UserManager;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.model.dto.EventFullDto;
import ru.practicum.model.dto.NewEventDto;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.LocationRepository;
import ru.practicum.service.EventService;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final UserManager userManager;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryManager categoryManager;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        User user = userManager.findUserById(userId);
        Category category = categoryManager.findCategoryById(newEventDto.getCategory());
        Location location = locationRepository.save(locationMapper.map(newEventDto.getLocation()));
        Event eventForSave = eventMapper.map(newEventDto, category, location, user, StateEnum.PENDING);
        Event event = eventRepository.save(eventForSave);
        return eventMapper.map(event);
    }
}
