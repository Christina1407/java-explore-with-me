package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.EventManager;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.NewCompilationDto;
import ru.practicum.repo.CompilationRepository;
import ru.practicum.service.CompilationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final EventManager eventManager;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Set<Long> eventsIds = newCompilationDto.getEvents();
        List<Event> events;
        //подборка может не содержать событий
        if (isNull(eventsIds) || eventsIds.isEmpty()) {
            events = new ArrayList<>();
        } else {
            //проверяем, что id всех событий из запроса найдены
            events = eventManager.findAllById(eventsIds);
            if (events.size() != eventsIds.size()) {
                log.error("Check ids of events = {}", eventsIds);
                throw new NotFoundException(String.format("Check ids of events = %s.", eventsIds));
            }
        }
        //Если в подборке есть опубликованные события, к ним добавляем просмотры и заявки
        eventManager.enrichEventsByViews(events);
        eventManager.enrichEventsByConfirmedRequests(events);
        Compilation compilationForSave = compilationMapper.map(newCompilationDto, events);
        return compilationMapper.map(compilationRepository.save(compilationForSave));
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        Compilation compilation = getCompilation(compilationId);
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto findCompilationById(Long compilationId) {
        Compilation compilation = getCompilation(compilationId);
        List<Event> events = compilation.getEvents();
        eventManager.enrichEventsByConfirmedRequests(events);
        eventManager.enrichEventsByViews(events);
        return compilationMapper.map(compilation);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Pageable pageable) {
        List<Compilation> compilations = isNull(pinned) ?
                compilationRepository.findAll(pageable).getContent() :
                compilationRepository.findAllByPinned(pinned, pageable);
        //TODO переделать
        compilations.forEach(
                compilation -> {
                    eventManager.enrichEventsByViews(compilation.getEvents());
                    eventManager.enrichEventsByConfirmedRequests(compilation.getEvents());
                }
        );
        return compilationMapper.map(compilations);
    }

    private Compilation getCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id = %d was not found", compilationId)));
    }

}
