package ru.practicum.service.impl.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.model.dto.NewCompilationDto;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.repo.CategoryRepository;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.LocationRepository;
import ru.practicum.repo.UserRepository;
import ru.practicum.service.CompilationService;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CompilationServiceImplTest {
    @Autowired
    private CompilationService compilationService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LocationRepository locationRepository;
    private Event event;
    private User initiator;
    private Category category;
    private Location location;

    @BeforeEach
    void setUp() {
        initiator = new User(null, "test@test.com", "Test Testov");
        initiator = userRepository.save(initiator);
        category = new Category(null, "test");
        category = categoryRepository.save(category);
        location = new Location(null, -85.1388F, 81.6359F);
        location = locationRepository.save(location);
        event = Event.builder()
                .annotation("Annotation teeeeeeest")
                .category(category)
                .description("Description teeeeeeest")
                .eventDate(LocalDateTime.now().plusDays(5))
                .initiator(initiator)
                .location(location)
                .paid(true)
                .participantLimit(0)
                .requestModeration(false)
                .title("title")
                .state(StateEnum.PENDING)
                .build();
        event = eventRepository.save(event);
    }

    @Test
    void saveCompilationNotFoundEvents() {
        //before
        NewCompilationDto newCompilationDto = NewCompilationDto.builder()
                .events(Set.of(100500L, 999999L))
                .title("Ghost")
                .build();

        //then
        assertThatThrownBy(() -> compilationService.saveCompilation(newCompilationDto))
                .isInstanceOf(NotFoundException.class);
    }
}