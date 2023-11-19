package ru.practicum.service.impl.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.StatClient;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
import ru.practicum.model.dto.*;
import ru.practicum.model.enums.SortEnum;
import ru.practicum.model.enums.StateActionEnum;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.model.enums.StatusEnum;
import ru.practicum.repo.*;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EventServiceImplTest {

    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @MockBean
    private StatClient statClient;
    private Event event;
    private User initiator;
    private Category category;
    @Autowired
    private PlaceRepository placeRepository;

    @BeforeEach
    void setUp() {
        initiator = new User(null, "test@test.com", "Test Testov");
        initiator = userRepository.save(initiator);
        category = new Category(null, "test");
        category = categoryRepository.save(category);
        event = Event.builder()
                .annotation("Annotation teeeeeeest")
                .category(category)
                .description("Description teeeeeeest")
                .eventDate(LocalDateTime.now().plusDays(5))
                .initiator(initiator)
                .lat(55.6665)
                .lon(37.5326)
                .paid(true)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PENDING)
                .build();
        event = eventRepository.save(event);
    }

    @Test
    void findEventByIdPublicNotPublished() {
        //before
        //then
        assertThatThrownBy(() -> eventService.findEventByIdPublic(event.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateEventByNotInitiator() {
        //before
        User user = new User(null, "test1@test.com", "Not Initiator");
        user = userRepository.save(user);
        UpdateEventRequest updateEventRequest = UpdateEventRequest.builder().build();
        //then
        User finalUser = user;
        assertThatThrownBy(() -> eventService.updateEventByInitiator(finalUser.getId(), event.getId(), updateEventRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void publishNotPendingEventByAdmin() {
        //before
        event.setState(StateEnum.CANCELED);
        event = eventRepository.save(event);

        UpdateEventRequest updateEventRequest = UpdateEventRequest.builder()
                .stateAction(StateActionEnum.PUBLISH_EVENT)
                .build();
        //then
        assertThatThrownBy(() -> eventService.updateEventByAdmin(event.getId(), updateEventRequest))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void updateEventByInitiatorEventDateFail() {
        //before
        UpdateEventRequest updateEventRequest = UpdateEventRequest.builder()
                .eventDate(LocalDateTime.now().plusHours(1))
                .build();
        //then
        assertThatThrownBy(() -> eventService.updateEventByInitiator(initiator.getId(), event.getId(), updateEventRequest))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void updateEventByAdminNewParticipantLimitLessThanConfirmedRequests() {
        //before
        event.setState(StateEnum.PUBLISHED);
        event = eventRepository.save(event);

        User user = new User(null, "test1@test.com", "Requester1");
        user = userRepository.save(user);
        User user2 = new User(null, "test2@test.com", "Requester2");

        user2 = userRepository.save(user2);
        Request request1 = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(StatusEnum.CONFIRMED)
                .build();
        requestRepository.save(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user2)
                .status(StatusEnum.CONFIRMED)
                .build();
        requestRepository.save(request2);
        UpdateEventRequest updateEventRequest = UpdateEventRequest.builder()
                .participantLimit(1)
                .build();
        //then
        assertThatThrownBy(() -> eventService.updateEventByAdmin(event.getId(), updateEventRequest))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void findEventsPublicPaid() {
        //before
        Event publishedEvent1 = Event.builder()
                .annotation("Palaeoanthropologist")
                .category(category)
                .description("Indistinguishability")
                .eventDate(LocalDateTime.now().plusDays(6))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        publishedEvent1 = eventRepository.save(publishedEvent1);
        Event publishedEvent2 = Event.builder()
                .annotation("Золотопромышленность")
                .category(category)
                .description("Деревообрабатывающий")
                .eventDate(LocalDateTime.now().plusDays(5))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        publishedEvent2 = eventRepository.save(publishedEvent2);
        Event publishedEvent3 = Event.builder()
                .annotation("Лжесвидетельствовать")
                .category(category)
                .description("Предводительствовать")
                .eventDate(LocalDateTime.now().plusDays(5))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(false)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        eventRepository.save(publishedEvent3);

        ParamsForPublic paramsForPublic = ParamsForPublic.builder()
                .paid(true)
                .build();
        Pageable pageable = PageRequest.of(0 / 10, 10, Sort.by(Sort.Direction.ASC, SortEnum.EVENT_DATE.getName()));

        //when
        List<EventShortDto> events = eventService.findEventsPublic(paramsForPublic, new SearchArea(null, null, null), 0, 10, SortEnum.EVENT_DATE);
        //then
        assertThat(events).isNotEmpty();
        assertThat(events.size()).isEqualTo(2);
        assertThat(events.get(0).getAnnotation()).isEqualTo(publishedEvent2.getAnnotation());
        assertThat(events.get(1).getAnnotation()).isEqualTo(publishedEvent1.getAnnotation());
    }

    @Test
    void confirmOrRejectNotPendingRequestsByInitiatorOfEvent() {
        //before
        event.setState(StateEnum.PUBLISHED);
        event = eventRepository.save(event);

        User user = new User(null, "test1@test.com", "Requester1");
        user = userRepository.save(user);

        User user2 = new User(null, "test2@test.com", "Requester2");
        user2 = userRepository.save(user2);

        Request request1 = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(StatusEnum.CONFIRMED)
                .build();
        request1 = requestRepository.save(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user2)
                .status(StatusEnum.REJECTED)
                .build();
        request2 = requestRepository.save(request2);

        List<Long> requests = new ArrayList<>();
        requests.add(request1.getId());
        requests.add(request2.getId());
        EventRequestStatusUpdateRequest statusUpdate = EventRequestStatusUpdateRequest.builder()
                .requestIds(requests)
                .status(StatusEnum.CONFIRMED)
                .build();
        //then
        assertThatThrownBy(() -> eventService.confirmOrRejectRequestsByInitiatorOfEvent(initiator.getId(),
                event.getId(), statusUpdate))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void confirmOrRejectRequestsByInitiatorOfEventSuccess() {
        //before
        event.setState(StateEnum.PUBLISHED);
        event = eventRepository.save(event);

        User user = new User(null, "test1@test.com", "Requester1");
        user = userRepository.save(user);

        User user2 = new User(null, "test2@test.com", "Requester2");
        user2 = userRepository.save(user2);

        Request request1 = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(StatusEnum.PENDING)
                .build();
        request1 = requestRepository.save(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user2)
                .status(StatusEnum.PENDING)
                .build();
        request2 = requestRepository.save(request2);

        List<Long> requests = new ArrayList<>(List.of(request1.getId(), request2.getId()));
        EventRequestStatusUpdateRequest statusUpdate = EventRequestStatusUpdateRequest.builder()
                .requestIds(requests)
                .status(StatusEnum.CONFIRMED)
                .build();
        //when
        EventRequestStatusUpdateResult result = eventService.confirmOrRejectRequestsByInitiatorOfEvent(initiator.getId(),
                event.getId(), statusUpdate);
        //then
        assertThat(result.getConfirmedRequests()).isNotEmpty();
        assertThat(result.getConfirmedRequests().size()).isEqualTo(2);
        assertThat(result.getRejectedRequests()).isEmpty();
    }

    //Только события, у которых не исчерпан лимит запросов на участие:
    @Test
    void findEventsPublicOnlyAvailable() {
        //before
        User user1 = new User(null, "test1@test.com", "Requester1");
        user1 = userRepository.save(user1);
        User user2 = new User(null, "test2@test.com", "Requester2");
        user2 = userRepository.save(user2);
        User user3 = new User(null, "test3@test.com", "Requester3");
        user3 = userRepository.save(user3);

        //есть подтверждённые заявки и participantLimit = 0
        Event publishedEvent1 = Event.builder()
                .annotation("Palaeoanthropologist")
                .category(category)
                .description("Indistinguishability")
                .eventDate(LocalDateTime.now().plusDays(1))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        publishedEvent1 = eventRepository.save(publishedEvent1);
        Request request1 = Request.builder()
                .created(LocalDateTime.now())
                .event(publishedEvent1)
                .requester(user1)
                .status(StatusEnum.CONFIRMED)
                .build();
        requestRepository.save(request1);

        //participantLimit > кол-ва подтверждённых заявок
        Event publishedEvent2 = Event.builder()
                .annotation("Золотопромышленность")
                .category(category)
                .description("Деревообрабатывающий")
                .eventDate(LocalDateTime.now().plusDays(2))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(2)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        publishedEvent2 = eventRepository.save(publishedEvent2);
        Request request2 = Request.builder()
                .created(LocalDateTime.now())
                .event(publishedEvent2)
                .requester(user1)
                .status(StatusEnum.CONFIRMED)
                .build();
        requestRepository.save(request2);
        Request request3 = Request.builder()
                .created(LocalDateTime.now())
                .event(publishedEvent2)
                .requester(user2)
                .status(StatusEnum.PENDING)
                .build();
        requestRepository.save(request3);
        Request request7 = Request.builder()
                .created(LocalDateTime.now())
                .event(publishedEvent2)
                .requester(user3)
                .status(StatusEnum.PENDING)
                .build();
        requestRepository.save(request7);

        //Это событие не попадёт в выборку, так как participantLimit == кол-ву подтверждённых заявок
        Event publishedEvent3 = Event.builder()
                .annotation("Лжесвидетельствовать")
                .category(category)
                .description("Предводительствовать")
                .eventDate(LocalDateTime.now().plusDays(3))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(false)
                .participantLimit(1)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        eventRepository.save(publishedEvent3);
        Request request4 = Request.builder()
                .created(LocalDateTime.now())
                .event(publishedEvent3)
                .requester(user1)
                .status(StatusEnum.CONFIRMED)
                .build();
        requestRepository.save(request4);

        //нет подтверждённых заявок на участие
        Event publishedEvent4 = Event.builder()
                .annotation("Навсегда ничего не бывает.")
                .category(category)
                .description("Л.Н.Толстой Война и мир")
                .eventDate(LocalDateTime.now().plusDays(4))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(false)
                .participantLimit(1)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        eventRepository.save(publishedEvent4);
        Request request5 = Request.builder()
                .created(LocalDateTime.now())
                .event(publishedEvent4)
                .requester(user1)
                .status(StatusEnum.PENDING)
                .build();
        requestRepository.save(request5);
        Request request6 = Request.builder()
                .created(LocalDateTime.now())
                .event(publishedEvent4)
                .requester(user2)
                .status(StatusEnum.PENDING)
                .build();
        requestRepository.save(request6);

        //нет заявок на участие и participantLimit != 0
        Event publishedEvent5 = Event.builder()
                .annotation("Живи и ошибайся. В этом жизнь.")
                .category(category)
                .description("Разум бессилен перед криком сердца")
                .eventDate(LocalDateTime.now().plusDays(5))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(false)
                .participantLimit(10)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        eventRepository.save(publishedEvent5);

        //нет заявок на участие и participantLimit = 0
        Event publishedEvent6 = Event.builder()
                .annotation("Всегда пишите код так, будто сопровождать его будет склонный к насилию психопат, который знает, где вы живете.")
                .category(category)
                .description("Простота — залог надежности")
                .eventDate(LocalDateTime.now().plusDays(6))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(false)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        eventRepository.save(publishedEvent6);

        ParamsForPublic paramsForPublic = ParamsForPublic.builder()
                .onlyAvailable(true)
                .build();
        Pageable pageable = PageRequest.of(0 / 10, 10, Sort.by(Sort.Direction.ASC, SortEnum.EVENT_DATE.getName()));

        //when
        List<EventShortDto> events = eventService.findEventsPublic(paramsForPublic, new SearchArea(null, null, null), 0, 10, SortEnum.EVENT_DATE);
        //then
        assertThat(events).isNotEmpty();
        assertThat(events.size()).isEqualTo(5);
        assertThat(events.get(0).getAnnotation()).isEqualTo(publishedEvent1.getAnnotation());
        assertThat(events.get(1).getAnnotation()).isEqualTo(publishedEvent2.getAnnotation());
        assertThat(events.get(4).getAnnotation()).isEqualTo(publishedEvent6.getAnnotation());
    }

    @Test
    void findEventsAdminCategory() {
        //before
        Event event1 = Event.builder()
                .annotation("Palaeoanthropologist")
                .category(category)
                .description("Indistinguishability")
                .eventDate(LocalDateTime.now().plusDays(1))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.CANCELED)
                .build();
        event1 = eventRepository.save(event1);

        Event event2 = Event.builder()
                .annotation("Золотопромышленность")
                .category(category)
                .description("Деревообрабатывающий")
                .eventDate(LocalDateTime.now().plusDays(2))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(2)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        event2 = eventRepository.save(event2);


        Category category1 = new Category(null, "test1");
        category1 = categoryRepository.save(category1);
        //Это событие не попадёт в выборку, так как другая категория
        Event event3 = Event.builder()
                .annotation("Лжесвидетельствовать")
                .category(category1)
                .description("Предводительствовать")
                .eventDate(LocalDateTime.now().plusDays(3))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(false)
                .participantLimit(1)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        eventRepository.save(event3);

        ParamsForAdmin params = new ParamsForAdmin();
        params.setCategories(List.of(category.getId()));
        Pageable pageable = PageRequest.of(0 / 10, 10, Sort.by(Sort.Direction.ASC, "id"));

        //when
        List<EventFullDto> events = eventService.findEventsByAdmin(params, pageable, new SearchArea(null, null, null));

        //then
        assertThat(events).isNotEmpty();
        assertThat(events.size()).isEqualTo(3);
        assertThat(events.get(0).getAnnotation()).isEqualTo(event.getAnnotation());
        assertThat(events.get(1).getAnnotation()).isEqualTo(event1.getAnnotation());
        assertThat(events.get(2).getAnnotation()).isEqualTo(event2.getAnnotation());
    }

    @Test
    void findEventsAdminState() {
        //before
        Event event1 = Event.builder()
                .annotation("Palaeoanthropologist")
                .category(category)
                .description("Indistinguishability")
                .eventDate(LocalDateTime.now().plusDays(1))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.CANCELED)
                .build();
        event1 = eventRepository.save(event1);

        Event event2 = Event.builder()
                .annotation("Золотопромышленность")
                .category(category)
                .description("Деревообрабатывающий")
                .eventDate(LocalDateTime.now().plusDays(2))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(2)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        eventRepository.save(event2);

        Category category1 = new Category(null, "test1");
        category1 = categoryRepository.save(category1);
        Event event3 = Event.builder()
                .annotation("Лжесвидетельствовать")
                .category(category1)
                .description("Предводительствовать")
                .eventDate(LocalDateTime.now().plusDays(3))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(false)
                .participantLimit(1)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .build();
        eventRepository.save(event3);

        ParamsForAdmin params = ParamsForAdmin.builder()
                .states(List.of(StateEnum.CANCELED))
                .build();
        Pageable pageable = PageRequest.of(0 / 10, 10, Sort.by(Sort.Direction.ASC, "id"));

        //when
        List<EventFullDto> events = eventService.findEventsByAdmin(params, pageable, new SearchArea(null, null, null));
        //then
        assertThat(events).isNotEmpty();
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.get(0).getAnnotation()).isEqualTo(event1.getAnnotation());

        //В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
        //before
        params.setCategories(List.of(category1.getId()));
        //when
        List<EventFullDto> events2 = eventService.findEventsByAdmin(params, pageable, new SearchArea(null, null, null));
        //then
        assertThat(events2).isEmpty();
    }

    @Test
    void findEventsAdminPlace() {
        //before
        Place place1 = Place.builder()
                .latitude(55.729949)
                .longitude(37.601735)
                .radius(800)
                .name("Парк")
                .type(new PlaceType(1L, "A"))
                .build();
        place1 = placeRepository.save(place1);

        Place place2 = Place.builder()
                .latitude(55.6665)
                .longitude(37.5326)
                .radius(500)
                .name("Парк2")
                .type(new PlaceType(2L, "B"))
                .build();
        place2 = placeRepository.save(place2);
        Event event1 = Event.builder()
                .annotation("Palaeoanthropologist")
                .category(category)
                .description("Indistinguishability")
                .eventDate(LocalDateTime.now().plusDays(1))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.729949)
                .lon(37.601735)
                .paid(true)
                .participantLimit(0)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.CANCELED)
                .places(List.of(place1))
                .build();
        event1 = eventRepository.save(event1);

        Event event2 = Event.builder()
                .annotation("Золотопромышленность")
                .category(category)
                .description("Деревообрабатывающий")
                .eventDate(LocalDateTime.now().plusDays(2))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.6665)
                .lon(37.5326)
                .paid(true)
                .participantLimit(2)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .places(List.of(place2))
                .build();
        event2 = eventRepository.save(event2);

        Category category1 = new Category(null, "test1");
        category1 = categoryRepository.save(category1);
        Event event3 = Event.builder()
                .annotation("Лжесвидетельствовать")
                .category(category1)
                .description("Предводительствовать")
                .eventDate(LocalDateTime.now().plusDays(3))
                .publishedOn(LocalDateTime.now())
                .initiator(initiator)
                .lat(55.6665)
                .lon(37.5326)
                .paid(false)
                .participantLimit(1)
                .requestModeration(true)
                .title("title")
                .state(StateEnum.PUBLISHED)
                .places(List.of(place2))
                .build();
        event3 = eventRepository.save(event3);

        ParamsForAdmin params = ParamsForAdmin.builder().build();
        params.setPlaces(List.of(place1.getId()));
        Pageable pageable = PageRequest.of(0 / 10, 10, Sort.by(Sort.Direction.ASC, "id"));

        //when
        List<EventFullDto> events = eventService.findEventsByAdmin(params, pageable, new SearchArea(null, null, null));
        //then
        assertThat(events).isNotEmpty();
        assertThat(events.size()).isEqualTo(1);
        assertThat(events.get(0).getAnnotation()).isEqualTo(event1.getAnnotation());


        //before
        params.setPlaces(List.of(place2.getId()));
        //when
        List<EventFullDto> events2 = eventService.findEventsByAdmin(params, pageable, new SearchArea(null, null, null));
        //then
        assertThat(events2).isNotEmpty();
        assertThat(events2.size()).isEqualTo(2);
        assertThat(events2.get(0).getAnnotation()).isEqualTo(event2.getAnnotation());
    }
}
