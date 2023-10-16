package ru.practicum.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.StatDtoResponse;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("select new ru.practicum.StatDtoResponse(h.app.name, h.uri, count(distinct h.ip)) "
            + "from Hit h where h.timestamp between :start and :end "
            + "group by h.app.name, h.uri "
            + "order by count(distinct h.ip) desc")
    List<StatDtoResponse> findAllUrisUnique(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select new ru.practicum.StatDtoResponse(h.app.name, h.uri, count(h.ip)) "
            + "from Hit h where h.timestamp between :start and :end "
            + "group by h.app.name, h.uri "
            + "order by count(h.ip) desc")
    List<StatDtoResponse> findAllUris(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("select new ru.practicum.StatDtoResponse(h.app.name, h.uri, count(distinct h.ip)) "
            + "from Hit h where h.timestamp between :start and :end "
            + "and uri in :uris "
            + "group by h.app.name, h.uri "
            + "order by count(distinct h.ip) desc")
    List<StatDtoResponse> findUniqueUriIn(LocalDateTime start, LocalDateTime end, List<String> uris, Pageable pageable);

    @Query("select new ru.practicum.StatDtoResponse(h.app.name, h.uri, count(h.ip)) "
            + "from Hit h where h.timestamp between :start and :end "
            + "and uri in :uris "
            + "group by h.app.name, h.uri "
            + "order by count(h.ip) desc")
   List<StatDtoResponse> findUriIn(LocalDateTime start, LocalDateTime end, List<String> uris, Pageable pageable);

}
