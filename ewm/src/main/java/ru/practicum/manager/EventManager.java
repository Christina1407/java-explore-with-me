package ru.practicum.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.StatClient;
import ru.practicum.StatDtoResponse;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.enums.StateEnum;
import ru.practicum.model.enums.StatusEnum;
import ru.practicum.repo.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@AllArgsConstructor
public class EventManager {
    private final EventRepository eventRepository;
    private final StatClient statClient;

    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id = %d was not found", eventId)));
    }

    public List<Event> findAllById(Set<Long> eventIds) {
        return eventRepository.findAllById(eventIds);
    }

    public boolean isParticipantLimitHasBeenReached(Event event) {
        return event.getParticipantLimit() != 0 && event.getParticipantLimit() == event.getRequests().stream()
                .filter(request -> request.getStatus().equals(StatusEnum.CONFIRMED))
                .count();
    }

    public long getQuantityOfConfirmedRequests(Event event) {
        return event.getRequests().stream()
                .filter(request -> request.getStatus().equals(StatusEnum.CONFIRMED))
                .count();
    }
    public void enrichEventByConfirmedRequests(Event event) {
        if (event.getState().equals(StateEnum.PUBLISHED)) {
            event.setConfirmedRequests(getQuantityOfConfirmedRequests(event));
        }
    }

    public void enrichEventsByConfirmedRequests(List<Event> eventList) {
        //подтверждённые заявки могут быть только у опубликованных событий
        List<Event> publishedEventList = getPublishedEvents(eventList);
        if (publishedEventList == null) return;
        publishedEventList.forEach(
                event -> {
                    event.setConfirmedRequests(getQuantityOfConfirmedRequests(event));
                }
        );
    }

    public void enrichEventByViews(Event event) {
        if (event.getState().equals(StateEnum.PUBLISHED)) {
            //URI как при сохранении статистики. Например "/events/4"
            String requestURI = "/events/" + event.getId();
            List<StatDtoResponse> stats = statClient.getStats(event.getPublishedOn(), LocalDateTime.now().plusDays(365), List.of(requestURI), true);
            long views = 0;
            if (!stats.isEmpty()) {
                views = stats.get(0).getHits();
            }
            event.setViews(views);
        }
    }

    public void enrichEventsByViews(List<Event> eventList) {
         //просмотры могут быть только у опубликованных событий
        List<Event> publishedEventList = getPublishedEvents(eventList);
        if (publishedEventList == null) return;

        List<String> requestURI = publishedEventList.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        //ищем самую раннюю дату публикации
        LocalDateTime start = null;
        for (Event event : publishedEventList) {
            if (isNull(start)) {
                start = event.getPublishedOn();
            }
            if (event.getPublishedOn().isBefore(start)) {
                start = event.getPublishedOn();
            }
        }
        Map<String, Long> stats = statClient.getStats(start, LocalDateTime.now().plusDays(365),
                        requestURI, true).stream()
                .collect(Collectors.toMap(StatDtoResponse::getUri, StatDtoResponse::getHits));
        publishedEventList.forEach(
                event -> {
                    event.setViews(stats.get("/events/" + event.getId()));
                }
        );
    }

    private List<Event> getPublishedEvents(List<Event> eventList) {
        List<Event> publishedEventList = eventList.stream()
                .filter(event -> event.getState().equals(StateEnum.PUBLISHED))
                .collect(Collectors.toList());
        if (publishedEventList.isEmpty()) {
            return null;
        }
        return publishedEventList;
    }
}
