package ru.practicum.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.App;
import ru.practicum.model.Hit;
import ru.practicum.model.dto.HitDto;

@Mapper(componentModel = "spring")
public interface HitMapper {


    @Mapping(target = "timestamp", source = "hitDto.timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "app", source = "sourceApp")
    Hit map(HitDto hitDto, App sourceApp);


    @Mapping(target = "timestamp", source = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "app", source = "app.name")
    HitDto map(Hit hit);
}
