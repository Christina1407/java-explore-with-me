package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
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

import java.util.List;
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
        Request requestForSave;

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
        //TODO или если запрос 'REJECTED' или 'CANCELED', то можно?
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.error("Request with eventId = {} and requesterId = {} already exists", eventId, userId);
            throw new ConflictException(String.format("Request with eventId = %d and requesterId = %d already exists", eventId, userId),
                    "Integrity constraint has been violated.");
        }

        //Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        long quantityOfConfirmedRequests = eventManager.getQuantityOfConfirmedRequests(event);
        if (eventManager.isParticipantLimitHasBeenReached(event)) {
            log.error("Event id = {} participant limit = {} and quantityOfConfirmedRequests = {}", eventId, event.getParticipantLimit(), quantityOfConfirmedRequests);
            throw new ConflictException(String.format("Event id = %d participant limit = %d and quantityOfConfirmedRequests = %d." +
                    " The participant limit has been reached", eventId, event.getParticipantLimit(), quantityOfConfirmedRequests),
                    "For the requested operation the conditions are not met.");
        }

        //Если для события лимит заявок равен 0 или отключена премодерация заявок, то подтверждение заявок не требуется
        if (event.getParticipantLimit() == 0 || Boolean.FALSE.equals(event.getRequestModeration())) {
            requestForSave = requestRepository.save(requestMapper.map(requester, event, StatusEnum.CONFIRMED));
            return requestMapper.map(requestForSave);
        }

        requestForSave = requestRepository.save(requestMapper.map(requester, event, StatusEnum.PENDING));
        return requestMapper.map(requestForSave);
    }

    @Override
    public List<ParticipationRequestDto> findRequests(Long userId) {
        userManager.findUserById(userId);
        return requestMapper.map(requestRepository.findByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto cancelRequestByRequester(Long userId, Long requestId) {
        userManager.findUserById(userId);
        Request requestForCancel = findRequestById(requestId);
        checkRequester(userId, requestForCancel);
        requestForCancel.setStatus(StatusEnum.CANCELED);
        return requestMapper.map(requestForCancel);
    }

    private void checkRequester(Long userId, Request request) {
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            log.error("Пользователь c id = {} не инициатор запроса id = {}.", userId, request.getId());
            throw new NotFoundException(String.format("Пользователь c id = %d не инициатор запроса id = %d.", userId, request.getId()));
        }
    }

    private Request findRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request with id =  %d was not found", requestId)));
    }


}
