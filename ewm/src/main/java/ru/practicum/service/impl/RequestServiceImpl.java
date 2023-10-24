package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.manager.EventManager;
import ru.practicum.manager.UserManager;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.dto.ParticipationRequestDto;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.model.enums.StatusEnum;
import ru.practicum.repo.RequestRepository;
import ru.practicum.service.RequestService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final EventManager eventManager;
    private final UserManager userManager;
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;

    @Override
    public ParticipationRequestDto saveRequest(Long userId, Long eventId) {
        User requester = userManager.findUserById(userId);
        Event event = eventManager.findEventById(eventId);

        //Инициатор события не может добавить запрос на участие в своём событии
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            log.error("User id = {} is the initiator of the event id = {}. Initiator cannot add a request to participate in his event", userId, eventId);
            throw new ConflictException(String.format("User id = %d is the initiator of the event id = %d. " +
                    "Initiator cannot add a request to participate in his event", userId, eventId),
                    "For the requested operation the conditions are not met.");
        }
        //Нельзя участвовать в неопубликованном событии
        if (!event.getState().equals(StateEnum.PUBLISHED)) {
            log.error("Event id = {} state = {}. You cannot participate in an unpublished event", eventId, event.getState());
            throw new ConflictException(String.format("Event id = %d state = %s." +
                    " You cannot participate in an unpublished event", eventId, event.getState()),
                    "For the requested operation the conditions are not met.");
        }
        //Нельзя добавить повторный запрос
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.error("Request with eventId = {} and requesterId = {} already exists", eventId, userId);
            throw new ConflictException(String.format("Request with eventId = %d and requesterId = %d already exists", eventId, userId),
                    "Integrity constraint has been violated.");
        }
        //Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        //TODO переделать. Ноль означает отсутствие ограничения. Нужно проверять кол-во подтв. запросов на участие и сравнивать с ParticipantLimit
//        if (event.getParticipantLimit() == 0) {
//            log.error("Event id = {} participant limit = {}", eventId, event.getParticipantLimit());
//            throw new ConflictException(String.format("Event id = %d participant limit = %d." +
//                    " The event has reached the ParticipantLimit", eventId, event.getParticipantLimit()),
//                    "For the requested operation the conditions are not met.");
//        }

        //Если для события отключена премодерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        Request requestForSave;
        if (event.getRequestModeration()) {
            requestForSave = requestRepository.save(requestMapper.map(requester, event, StatusEnum.PENDING));
        } else {
            requestForSave = requestRepository.save(requestMapper.map(requester, event, StatusEnum.CONFIRMED));
        }
        return requestMapper.map(requestForSave);

    }
}
