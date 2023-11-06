package ru.practicum.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.aspect.SaveStatistic;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.CategoryManager;
import ru.practicum.manager.EventManager;
import ru.practicum.manager.PlaceManager;
import ru.practicum.manager.UserManager;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.model.dto.*;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.model.enums.StateActionEnum;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.model.enums.StatusEnum;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.RequestRepository;
import ru.practicum.service.EventService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventServiceImpl implements EventService {
    private final UserManager userManager;
    private final EventManager eventManager;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final CategoryManager categoryManager;
    private final RequestMapper requestMapper;
    private final EntityManager entityManager;
    private final PlaceManager placeManager;

    @Override
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        User user = userManager.findUserById(userId);
        Category category = categoryManager.findCategoryById(newEventDto.getCategory());
        //проверка, что заданные места есть в базе
        List<Place> places = placeManager.findPlaces(newEventDto.getPlaces());
        //проверка, что координаты попадают в область покрытия мест
        placeManager.checkPlaces(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon(), places);
        Event eventForSave = eventMapper.map(newEventDto, category, user, StateEnum.PENDING, places);
        Event event = eventRepository.save(eventForSave);
        return eventMapper.map(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto findInitiatorEventById(Long userId, Long eventId) {
        Event event = eventManager.findEventById(eventId);
        //Проверка, что информацию получает инициатор события
        checkInitiator(userId, event);
        //Если событие опубликовано, то нужно добавить просмотры и запросы
        eventManager.enrichEventByConfirmedRequests(event);
        eventManager.enrichEventByViews(event);
        return eventMapper.map(event);
    }

    @Override
    @Transactional(readOnly = true)
    @SaveStatistic
    public EventFullDto findEventByIdPublic(Long eventId) {
        Event event = eventManager.findEventById(eventId);

        //Событие должно быть PUBLISHED
        if (!event.getState().equals(StateEnum.PUBLISHED)) {
            log.error("State of event id = {} is {}. You can get only PUBLISHED events in this endpoint", eventId, event.getState());
            throw new NotFoundException(String.format("State of event id = %d is not PUBLISHED", eventId));
        }
        //Информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
        eventManager.enrichEventByConfirmedRequests(event);
        eventManager.enrichEventByViews(event);

        //Информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики

        return eventMapper.map(event);
    }

    @Override
    @Transactional(readOnly = true)
    @SaveStatistic
    //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    //информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
    public List<EventShortDto> findEventsPublic(ParamsForPublic params, SearchArea searchArea, int from, int size, SortEnum sort) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = new ArrayList<>();

        if (sort.equals(SortEnum.VIEWS)) {
            //Получаем все события с учётом фильтров без пагинации
            eventRepository.findAll(getPredicateByParamsForPublic(params)).forEach(events::add);
            //Если задана область поиска, выбираем события, которые в неё попадают
            events = filterEventsBySearchArea(events, searchArea);
            //Добавляем просмотры
            eventManager.enrichEventsByViews(events);
            //Сортируем по количеству просмотров
            events.sort(Comparator.comparingLong(Event::getViews).reversed());
            //Выбираем необходимую страницу
            events = createPageContentFromEvents(events, pageable);
        } else {
            //Получаем все события с учётом фильтров без пагинации, отсортированные по дате события
            eventRepository.findAll(getPredicateByParamsForPublic(params), Sort.by(Sort.Direction.ASC, sort.getName())).forEach(events::add);
            //Если задана область поиска, выбираем события, которые в неё попадают
            events = filterEventsBySearchArea(events, searchArea);
            //Выбираем необходимую страницу
            events = createPageContentFromEvents(events, pageable);
            //Добавляем просмотры
            eventManager.enrichEventsByViews(events);
        }
        //Добавляем подтвержденные заявки
        eventManager.enrichEventsByConfirmedRequests(events);
        return eventMapper.mapToEventShortDtoList(events);
    }

    @Override
    public EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventRequest updateEventUserRequest) {
        Event eventForUpdate = eventManager.findEventById(eventId);
        checkInitiator(userId, eventForUpdate);

        //Изменить можно только отмененные события или события в состоянии ожидания модерации
        if (nonNull(eventForUpdate.getState()) && eventForUpdate.getState().equals(StateEnum.PUBLISHED)) {
            log.error("You can update the event if state = {} or {}. Event id = {} has state = {}",
                    StateEnum.PENDING, StateEnum.CANCELED, eventId, eventForUpdate.getState());
            throw new ConflictException(String.format("Event id = %d has state = %s." +
                    " You can update the event if state = %s, %s", eventId, eventForUpdate.getState(), StateEnum.PENDING, StateEnum.CANCELED),
                    "For the requested operation the conditions are not met.");
        }

        //Дата и время, на которые намечено событие не может быть раньше, чем через два часа от текущего момента
        checkEventDate(updateEventUserRequest, 2);

        //Новое состояние события
        StateActionEnum stateAction = updateEventUserRequest.getStateAction();
        if (nonNull(stateAction)) {
            if (stateAction.equals(StateActionEnum.SEND_TO_REVIEW)) {
                eventForUpdate.setState(StateEnum.PENDING);
            } else if (stateAction.equals(StateActionEnum.CANCEL_REVIEW)) {
                eventForUpdate.setState(StateEnum.CANCELED);
            }
        }

        //Если прилетел айдишник категории, проверяем, что есть такая категория
        Category category = getCategory(updateEventUserRequest);

        eventMapper.update(updateEventUserRequest, category, eventForUpdate);
        return eventMapper.map(eventForUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findInitiatorEvents(Long userId, Pageable pageable) {
        userManager.findUserById(userId);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        eventManager.enrichEventsByViews(events);
        eventManager.enrichEventsByConfirmedRequests(events);
        return eventMapper.mapToEventShortDtoList(events);
    }

    @Override
    public List<EventFullDto> findEventsByAdmin(ParamsForAdmin params, Pageable pageable, SearchArea searchArea) {
        List<Event> events = new ArrayList<>();
        //Получаем все события, подходящие под условия, отсортированные по id
        eventRepository.findAll(getPredicateByParamsForAdmin(params), pageable.getSort()).forEach(events::add);
        //Если задана searchArea, то выбираем из найденных событий те, которые попадают в область поиска
        events = filterEventsBySearchArea(events, searchArea);
        //Берём требуемую страницу
        events = createPageContentFromEvents(events, pageable);
        //Добавляем просмотры и подтверждённые заявки
        eventManager.enrichEventsByViews(events);
        eventManager.enrichEventsByConfirmedRequests(events);
        return eventMapper.mapToEventFullDtoList(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateEventAdminRequest) {
        Event eventForUpdate = eventManager.findEventById(eventId);

        //Дата начала изменяемого события должна быть не ранее чем за час от даты публикации
        checkEventDate(updateEventAdminRequest, 1);

        //Если меняется лимит участников(не на значение 0), то он не может быть меньше кол-ва уже подтверждённых заявок
        checkNewParticipantLimit(eventId, updateEventAdminRequest, eventForUpdate);

        //TODO
        //Изменение координат и мест
        LocationDto location = updateEventAdminRequest.getLocation();
        List<Long> places = updateEventAdminRequest.getPlaces();

        //меняется location, places null
        if (nonNull(location)) {
            if (!eventForUpdate.getLat().equals(location.getLat()) || !eventForUpdate.getLon().equals(location.getLon())) {
                if (isNull(places)) {
                    //проверка, что новые координаты попадают в область покрытия мест, которые уже были у события
                    placeManager.checkPlaces(location.getLat(), location.getLon(), eventForUpdate.getPlaces());
                }
                // places меняются
                if (!places.isEmpty()) {
                    //проверка, что новые места есть в базе
                    List<Place> updatePlaces = placeManager.findPlaces(places);
                    //проверка, что новые координаты попадают в область покрытия новых мест
                    placeManager.checkPlaces(location.getLat(), location.getLon(), updatePlaces);
                }
            }
        } else {
            //не меняется location, меняются places
            if (nonNull(places) && !places.isEmpty()) {
                //проверка, что новые места есть в базе
                List<Place> updatePlaces = placeManager.findPlaces(places);
                //проверка, что старые координаты попадают в область покрытия новых мест
                placeManager.checkPlaces(eventForUpdate.getLat(), eventForUpdate.getLon(), updatePlaces);
            }
        }

        //Новое состояние события
        StateActionEnum stateAction = updateEventAdminRequest.getStateAction();
        if (nonNull(stateAction)) {
            //Событие можно опубликовать, только если оно в состоянии ожидания публикации
            //Событие можно отклонить, только если оно еще не опубликовано
            if (stateAction.equals(StateActionEnum.PUBLISH_EVENT)) {
                publishPendingEventOrThrow(eventId, eventForUpdate);
            } else if (stateAction.equals(StateActionEnum.REJECT_EVENT)) {
                cancelNotPublishedEventOrThrow(eventId, eventForUpdate);
            }
        }

        //Если прилетел айдишник категории, проверяем, что такая категория есть
        Category category = getCategory(updateEventAdminRequest);

        eventMapper.update(updateEventAdminRequest, category, eventForUpdate);
        eventManager.enrichEventByViews(eventForUpdate);
        eventManager.enrichEventByConfirmedRequests(eventForUpdate);
        return eventMapper.map(eventForUpdate);
    }

    @Override
    public EventRequestStatusUpdateResult confirmOrRejectRequestsByInitiatorOfEvent(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        Event event = eventManager.findEventById(eventId);
        checkInitiator(userId, event);

        //Проверяем все ли айдишники запросов на участие из eventRequestStatusUpdateRequest относятся к событию eventId
        List<Request> eventsRequests = event.getRequests();
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        //Лист реквестов из запроса, которые созданы к событию
        List<Request> checkedRequests = eventsRequests.stream()
                .filter(request -> requestIds.contains(request.getId()))
                .sorted(Comparator.comparing(request -> requestIds.indexOf(request.getId())))
                .collect(Collectors.toList());
        if (checkedRequests.size() != requestIds.size()) {
            log.error("RequestIds не были найдены среди ids созданных запросов на участие к событию id = {}", eventId);
            throw new NotFoundException(String.format("RequestIds не были найдены среди ids созданных запросов на участие к событию id = %d.", eventId));
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

        //Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (eventManager.isParticipantLimitHasBeenReached(event)) {
            log.error("Event id = {} participantLimitHasBeenReached {}", eventId, eventManager.isParticipantLimitHasBeenReached(event));
            throw new ConflictException(String.format("Event id = %d the participant limit has been reached.", eventId),
                    "For the requested operation the conditions are not met.");
        }

        //Если при подтверждении заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки из запроса отклоняются
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

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findRequestsByInitiatorOfEvent(Long userId, Long eventId) {
        Event event = eventManager.findEventById(eventId);
        checkInitiator(userId, event);
        return requestMapper.map(requestRepository.findByEventId(eventId));
    }

    private void checkInitiator(Long userId, Event event) {
        userManager.findUserById(userId);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            log.error("Пользователь c id = {} хочет получить данные события id = {}. " +
                    "Данные о событии по этому эндпоинту доступны только инициатору.", userId, event.getId());
            throw new NotFoundException(String.format("Пользователь c id = %d не инициатор события id = %d.", userId, event.getId()));
        }
    }

    private void checkNewParticipantLimit(Long eventId, UpdateEventRequest updateEventAdminRequest, Event eventForUpdate) {
        Integer newParticipantLimit = updateEventAdminRequest.getParticipantLimit();
        if (nonNull(newParticipantLimit) && newParticipantLimit != 0 &&
                newParticipantLimit < eventManager.getQuantityOfConfirmedRequests(eventForUpdate)) {
            log.error("Event id = {} quantity of confirmed requests = {} > NewParticipantLimit = {}",
                    eventId, eventManager.getQuantityOfConfirmedRequests(eventForUpdate), newParticipantLimit);
            throw new ConflictException(String.format("Event id = %d confirmed requests = %d" +
                    " > NewParticipantLimit = %d", eventId, eventManager.getQuantityOfConfirmedRequests(eventForUpdate), newParticipantLimit),
                    "For the requested operation the conditions are not met.");
        }
    }

    private void cancelNotPublishedEventOrThrow(Long eventId, Event eventForUpdate) {
        if (!eventForUpdate.getState().equals(StateEnum.PUBLISHED)) {
            eventForUpdate.setState(StateEnum.CANCELED);
        } else {
            log.error("Event id = {} has state = {}. The event can be canceled only if state!= {}",
                    eventId, eventForUpdate.getState(), StateEnum.PUBLISHED);
            throw new ConflictException(String.format("Event id = %d has state = %s." +
                    " The event can be canceled only if state != %s", eventId, eventForUpdate.getState(), StateEnum.PUBLISHED),
                    "For the requested operation the conditions are not met.");
        }
    }

    private void publishPendingEventOrThrow(Long eventId, Event eventForUpdate) {
        if (eventForUpdate.getState().equals(StateEnum.PENDING)) {
            eventForUpdate.setPublishedOn(LocalDateTime.now());
            eventForUpdate.setState(StateEnum.PUBLISHED);
        } else {
            log.error("Event id = {} has state = {}. The event can be published only if state {}",
                    eventId, eventForUpdate.getState(), StateEnum.PENDING);
            throw new ConflictException(String.format("Event id = %d has state = %s." +
                    " The event can be published only if state = %s", eventId, eventForUpdate.getState(), StateEnum.PENDING),
                    "For the requested operation the conditions are not met.");
        }
    }

    private void checkEventDate(UpdateEventRequest updateEventAdminRequest, int minHoursDifference) {
        if (nonNull(updateEventAdminRequest.getEventDate()) &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(minHoursDifference))) {
            log.error("Difference between eventDate for change = {} and now = {} less than {} hour(s)",
                    updateEventAdminRequest.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), minHoursDifference);
            throw new ConflictException(String.format("Difference between eventDate for change = %s " +
                            "and now = %s less than %d hour(s)",
                    updateEventAdminRequest.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), minHoursDifference),
                    "For the requested operation the conditions are not met.");
        }
    }

    private Category getCategory(UpdateEventRequest updateEventRequest) {
        Category category = null;
        if (nonNull(updateEventRequest.getCategory())) {
            category = categoryManager.findCategoryById(updateEventRequest.getCategory());
        }
        return category;
    }

    //общая часть админ и паблик
    private void getPredicateByParams(GetEventsRequestParam params, BooleanBuilder where) {
        if (!CollectionUtils.isEmpty(params.getCategories())) {
            where.and(QEvent.event.category.id.in(params.getCategories()));
        }
        if (nonNull(params.getRangeStart()) && isNull(params.getRangeEnd())) {
            where.and(QEvent.event.eventDate.goe(params.getRangeStart()));
        }
        if (nonNull(params.getRangeEnd()) && isNull(params.getRangeStart())) {
            where.and(QEvent.event.eventDate.loe(params.getRangeEnd()));
        }
        if (nonNull(params.getRangeStart()) && nonNull(params.getRangeEnd())) {
            where.and(QEvent.event.eventDate.between(params.getRangeStart(), params.getRangeEnd()));
        }
        if (!CollectionUtils.isEmpty(params.getPlaces())) {
            where.and(QEvent.event.places.any().id.in(params.getPlaces()));
        }
    }

    private Predicate getPredicateByParamsForAdmin(ParamsForAdmin params) {
        BooleanBuilder where = new BooleanBuilder();
        getPredicateByParams(params, where);
        if (!CollectionUtils.isEmpty(params.getUsers())) {
            where.and(QEvent.event.initiator.id.in(params.getUsers()));
        }
        if (!CollectionUtils.isEmpty(params.getStates())) {
            where.and(QEvent.event.state.in(params.getStates()));
        }
        return where;
    }

    private Predicate getPredicateByParamsForPublic(ParamsForPublic params) {
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        BooleanBuilder where = new BooleanBuilder();
        getPredicateByParams(params, where);
        //В ответе должны быть только опубликованные события
        where.and(QEvent.event.state.eq(StateEnum.PUBLISHED));
        if (nonNull(params.getText())) {
            where.and(QEvent.event.annotation.containsIgnoreCase(params.getText())
                    .or(QEvent.event.description.containsIgnoreCase(params.getText())));
        }
        if (nonNull(params.getPaid())) {
            where.and(QEvent.event.paid.eq(params.getPaid()));
        }
        //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
        if (isNull(params.getRangeStart()) && isNull(params.getRangeEnd())) {
            where.and(QEvent.event.eventDate.goe(LocalDateTime.now()));
        }
        /*
        Только события, у которых не исчерпан лимит запросов на участие:
        1) participantLimit = 0;
        2) нет заявок на участие;
        4) нет подтверждённых заявок на участие;
        3) лимит больше кол-ва подтверждённых заявок.
         */
        if (params.isOnlyAvailable()) {
            QRequest request = QRequest.request;
            NumberExpression<Long> id = request.event.id;
            NumberExpression<Integer> limit = request.event.participantLimit;
            NumberExpression<Long> confirmed = request.count();

            //Получить все идентификаторы событий, у которых есть подтверждённые и их кол-во == лимиту участников
            List<Long> eventId = query.select(id, limit, confirmed)
                    .from(request)
                    .where(request.status.eq(StatusEnum.CONFIRMED))
                    .groupBy(id)
                    .having(limit.eq(confirmed.intValue()))
                    .fetch()
                    .stream()
                    .map(tuple -> tuple.get(id))
                    .collect(Collectors.toList());
            //нам подходят все остальные
            where.and(QEvent.event.id.notIn(eventId));
        }
        return where;
    }

    private List<Event> filterEventsBySearchArea(List<Event> events, SearchArea searchArea) {
        if (isNull(searchArea.getLat()) && isNull(searchArea.getLon()) && isNull(searchArea.getRadius())) {
            return events;
        }
        if (isNull(searchArea.getLat()) || isNull(searchArea.getLon()) || isNull(searchArea.getRadius())) {
            log.error("{} must have all fields", searchArea);
            throw new ConflictException(String.format("%s must have all fields", searchArea),
                    "For the requested operation the conditions are not met.");
        }
        return placeManager.filterEventsInSearchArea(searchArea, events);
    }

    private List<Event> createPageContentFromEvents(List<Event> events, Pageable pageable) {
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), events.size());
        final Page<Event> page = new PageImpl<>(events.subList(start, end), pageable, events.size());
        return page.getContent();
    }
}
