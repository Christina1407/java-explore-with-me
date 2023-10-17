package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.StatDtoResponse;
import ru.practicum.model.App;
import ru.practicum.model.StatRequestParams;
import ru.practicum.model.mapper.HitMapper;
import ru.practicum.repo.AppRepository;
import ru.practicum.repo.HitRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;
    private final AppRepository appRepository;

    @Override
    @Transactional
    public HitDto saveHit(HitDto hitDto) {
        String appName = hitDto.getApp();
        App app = appRepository.findByName(appName);
        //если приложения нет в базе, сохраняем новое приложение
        if (isNull(app)) {
            app = appRepository.save(App.builder()
                    .name(appName)
                    .build());
            log.info("Приложение {} сохранено", app);
        }
        return hitMapper.map(hitRepository.save(hitMapper.map(hitDto, app)));
    }

    @Override
    public List<StatDtoResponse> getStats(StatRequestParams params, Pageable pageable) {
        LocalDateTime start = params.getStart();
        LocalDateTime end = params.getEnd();
        List<String> uris = params.getUris();
        boolean unique = params.isUnique();
        //если uris - пустой лист или null, то возвращаем статитистику по всем uri постранично
        if (Objects.isNull(uris) || uris.isEmpty()) {
            log.info("Получение статистики unique = {} по посещениям всех uri за период start = {} end = {}", unique, start, end);
            if (unique) {
                return hitRepository.findAllUrisUnique(start, end, pageable);
            } else {
                return hitRepository.findAllUris(start, end, pageable);
            }
        } else {
            log.info("Получение статистики unique = {} по посещениям uri = {} за период start = {} end = {}", unique, uris, start, end);
            if (unique) {
                return hitRepository.findUniqueUriIn(start, end, uris, pageable);
            } else {
                return hitRepository.findUriIn(start, end, uris, pageable);
            }

        }

    }
}
