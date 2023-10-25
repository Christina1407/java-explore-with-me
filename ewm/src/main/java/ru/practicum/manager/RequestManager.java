package ru.practicum.manager;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Request;
import ru.practicum.repo.RequestRepository;

import java.util.List;

@Component
@AllArgsConstructor
public class RequestManager {
    private final RequestRepository requestRepository;

    public Request findRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Request with id =  %d was not found", requestId)));
    }
    public List<Request> findRequestsByIds(List<Long> requestIds) {
        return requestRepository.findByIdIn(requestIds);
    }

}
