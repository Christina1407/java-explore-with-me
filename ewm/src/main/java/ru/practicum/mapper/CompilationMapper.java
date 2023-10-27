package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.NewCompilationDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {
    @Mapping(target = "events", source = "eventList")
    @Mapping(target = "id", ignore = true)
    Compilation map(NewCompilationDto newCompilationDto, List<Event> eventList);

    CompilationDto map(Compilation compilation);
    List<CompilationDto> map (List<Compilation> compilations);
}
