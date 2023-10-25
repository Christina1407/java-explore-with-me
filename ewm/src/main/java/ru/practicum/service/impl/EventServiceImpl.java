package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.CategoryManager;
import ru.practicum.manager.EventManager;
import ru.practicum.manager.UserManager;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.model.dto.*;
import ru.practicum.model.enums.StateActionEnum;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.model.enums.StatusEnum;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.LocationRepository;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

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
    private final RequestMapper requestMapper;

    public static final int MIN_HOURS_DIFFERENCE = 1;

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
        return null;
        //return eventMapper.map(event, views);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto publicFindEventById(Long eventId, String ip, String requestURI) {
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        Event event = eventManager.findEventById(eventId);
        statClient.saveHit(requestURI, "ewm", ip, LocalDateTime.now());
        //TODO
        //событие должно быть PUBLISHED
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(GetEventsRequestParams params, Pageable pageable) {
        return null; //TODO QueryDSL
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event eventForUpdate = eventManager.findEventById(eventId);

        //Дата начала изменяемого события должна быть не ранее чем за час от даты публикации
        if (nonNull(updateEventAdminRequest.getEventDate()) &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(MIN_HOURS_DIFFERENCE))) {
            log.error("Difference between eventDate for change = {} and now = {} less than {} hour(s)",
                    updateEventAdminRequest.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), MIN_HOURS_DIFFERENCE);
            throw new ConflictException(String.format("Difference between eventDate for change = %s " +
                            "and now = %s less than %d hour(s)", updateEventAdminRequest.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), MIN_HOURS_DIFFERENCE),
                    "For the requested operation the conditions are not met.");
        }

        //Событие можно опубликовать/отклонить, только если оно в состоянии ожидания публикации
        if (!Objects.equals(eventForUpdate.getState(), StateEnum.PENDING)) {
            log.error("Event id = {} state = {}", eventId, eventForUpdate.getState());
            throw new ConflictException(String.format("Event id = %d state = %s." +
                    " The event can be published or cancel only if state = %s", eventId, eventForUpdate.getState(), StateEnum.PENDING),
                    "For the requested operation the conditions are not met.");
        }
        //Новое состояние события
        StateActionEnum stateAction = updateEventAdminRequest.getStateAction();
        if (Objects.equals(stateAction, StateActionEnum.PUBLISH_EVENT)) {
            eventForUpdate.setPublishedOn(LocalDateTime.now());
            eventForUpdate.setState(StateEnum.PUBLISHED);
        } else if (Objects.equals(stateAction, StateActionEnum.REJECT_EVENT)) {
            eventForUpdate.setState(StateEnum.CANCELED);
        }

        //Если прилетел айдишник категории, проверяем, что есть такая категория
        Category category = null;
        if (nonNull(updateEventAdminRequest.getCategory())) {
            category = categoryManager.findCategoryById(updateEventAdminRequest.getCategory());
        }
        eventMapper.updateByAdmin(updateEventAdminRequest, category, eventForUpdate);
        return eventMapper.map(eventForUpdate, 0L); //TODO views
    }

    @Override
    public EventRequestStatusUpdateResult confirmOrRejectRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userManager.findUserById(userId);
        Event event = eventManager.findEventById(eventId);
        checkInitiator(userId, event);
        List<Request> eventsRequests = event.getRequests();

        //проверяем все ли айдишники запросов на участие из eventRequestStatusUpdateRequest созданы к событию eventId
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        //лист реквестов из запроса, которые созданы к событию
        List<Request> checkedRequests = eventsRequests.stream()
                .filter(request -> requestIds.contains(request.getId()))
                .sorted(Comparator.comparing(request -> requestIds.indexOf(request.getId())))
                .collect(Collectors.toList());
        if (checkedRequests.size() != requestIds.size()) {
            log.error("RequestIds не были найдены среди ids созданных запросов на участие к событию id = {}", eventId);
            throw new NotFoundException(String.format("RequestIds не были найдены среди ids созданных запросов на участие к событию id = %d.", eventId));
        }

        //Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (eventManager.isParticipantLimitHasBeenReached(event)) {
            log.error("Event id = {} participantLimitHasBeenReached {}", eventId, eventManager.isParticipantLimitHasBeenReached(event));
            throw new ConflictException(String.format("Event id = %d the participant limit has been reached.", eventId),
                    "For the requested operation the conditions are not met.");
        }

        //Статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        List<Long> notPendingStatusRequestsIds = new ArrayList<>();
        checkedRequests.forEach(
                request -> {
                    if (!request.getStatus().equals(StatusEnum.PENDING)) {
                        notPendingStatusRequestsIds.add(request.getId());
                    }
                }
        );
        if (!notPendingStatusRequestsIds.isEmpty()) {
            log.error("Request(s) id(s) = {} status(es) is(are) not PENDING. You can change requests only with status PENDING", notPendingStatusRequestsIds);
            throw new ConflictException(String.format("Request(s) id(s) = %s status(es) is(are) not PENDING.", notPendingStatusRequestsIds),
                    "For the requested operation the conditions are not met.");

        }

        ////Если при подтверждении заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки(из запроса) отклоняются
        //TODO или не из запроса тоже отменять? А если лимит увеличат?
        checkedRequests.forEach(
                request -> request.setStatus(StatusEnum.REJECTED)
        );
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus().equals(StatusEnum.REJECTED)) {
            rejectedRequests.addAll(checkedRequests);
            return eventMapper.map(confirmedRequests, rejectedRequests);
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(StatusEnum.CONFIRMED)) {
            checkedRequests.forEach(
                    request -> {
                        if (!eventManager.isParticipantLimitHasBeenReached(event)) {
                            request.setStatus(StatusEnum.CONFIRMED);
                            confirmedRequests.add(request);
                        } else {
                            rejectedRequests.add(request);
                        }
                    }
            );
        }
        return eventMapper.map(confirmedRequests, rejectedRequests);
    }

    private void checkInitiator(Long userId, Event event) {
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            log.error("Пользователь c id = {} хочет получить данные события id = {}. " +
                    "Данные о событии по этому эндпоинту доступны только инициатору.", userId, event.getId());
            throw new NotFoundException(String.format("Пользователь c id = %d не инициатор события id = %d.", userId, event.getId()));
        }
    }


}
