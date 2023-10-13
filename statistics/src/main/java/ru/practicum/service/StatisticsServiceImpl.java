package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.model.App;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.mapper.HitMapper;
import ru.practicum.repo.AppRepository;
import ru.practicum.repo.HitRepository;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;
    private final AppRepository appRepository;
    @Override
    public HitDto saveHit(HitDto hitDto) {
        String appName = hitDto.getApp();
        App app = appRepository.findByName(appName);
        //если приложения нет в базе, сохраняем новое приложение
        if (isNull(app)) {
            app = appRepository.save(App.builder()
                    .name(appName)
                    .build());
        }
        return hitMapper.map(hitRepository.save(hitMapper.map(hitDto, app)));
    }
}
