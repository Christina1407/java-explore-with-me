package ru.practicum.service.impl.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.exception.ConflictException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.dto.ParticipationRequestDto;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.model.enums.StatusEnum;
import ru.practicum.repo.CategoryRepository;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.RequestRepository;
import ru.practicum.repo.UserRepository;
import ru.practicum.service.RequestService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestServiceImplTest {

    @Autowired
    private RequestService requestService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Event event;
    private User initiator;
    private Category category;
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
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .title("title")
                .state(StateEnum.PENDING)
                .build();
        event = eventRepository.save(event);
    }


    @Test
    void saveRequestByInitiator() {
        //before
        event.setState(StateEnum.PUBLISHED);
        event = eventRepository.save(event);
        //then
        assertThatThrownBy(() -> requestService.saveRequest(initiator.getId(), event.getId()))
                .isInstanceOf(ConflictException.class).hasMessage("User id = 1 is the initiator of the event id = 1. " +
                        "Initiator cannot add a request to participate in his event");

    }

    @Test
    void saveRequestNotPublishedEvent() {
        //before
        User user = new User(null, "test1@test.com", "Not Initiator");
        user = userRepository.save(user);

        //then
        User finalUser = user;
        assertThatThrownBy(() -> requestService.saveRequest(finalUser.getId(), event.getId()))
                .isInstanceOf(ConflictException.class).hasMessage("Event id = 1 state = PENDING. You cannot participate in an unpublished event");

    }

    @Test
    void saveRequestConfirmed() {
        //before
        event.setState(StateEnum.PUBLISHED);
        event = eventRepository.save(event);
        User user = new User(null, "test1@test.com", "Not Initiator");
        user = userRepository.save(user);

        //when
        User finalUser = user;
        ParticipationRequestDto request = requestService.saveRequest(finalUser.getId(), event.getId());
        //then
        assertThat(request).isNotNull();
        assertThat(request.getStatus()).isEqualTo(StatusEnum.CONFIRMED);
        assertThat(request.getRequester()).isEqualTo(user.getId());
    }

    @Test
    void saveRequestLimitReached() {
        //before
        event.setState(StateEnum.PUBLISHED);
        event.setParticipantLimit(1);
        event = eventRepository.save(event);
        User user = new User(null, "test1@test.com", "Not Initiator");
        user = userRepository.save(user);
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(StatusEnum.CONFIRMED)
                .build();
        requestRepository.save(request);
        User user2 = new User(null, "test2@test.com", "Not Initiator2");
        user2 = userRepository.save(user2);

        //when
        //then
        User finalUser = user2;
        assertThatThrownBy(() -> requestService.saveRequest(finalUser.getId(), event.getId()))
                .isInstanceOf(ConflictException.class).hasMessage("Event id = 1 participant limit = 1 and quantityOfConfirmedRequests = 1." +
                        " The participant limit has been reached");
    }

}