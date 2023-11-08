package ru.practicum.manager;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.Place;
import ru.practicum.model.dto.SearchArea;
import ru.practicum.repo.PlaceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@AllArgsConstructor
@Slf4j
public class PlaceManager {
    private final PlaceRepository placeRepository;

    public Place findPlaceByIdOrThrow(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow(() -> new NotFoundException(String.format("Place with id =  %d was not found", placeId)));
    }

    public List<Place> findPlaces(List<Long> placeIds) {
        if (nonNull(placeIds)) {
            List<Place> places = placeRepository.findAllById(placeIds);
            if (places.size() != new HashSet<>(placeIds).size()) {
                log.error("Check ids of places = {}", placeIds);
                throw new NotFoundException(String.format("Check ids of events = %s.", placeIds));
            }
            return places;
        }
        return new ArrayList<>();
    }

    public List<Place> findPlacesByCoordinates(Double lat, Double lon) {
        //Поиск мест, в область которых попадают заданные координаты. Отсортированы по отдалённости
        List<Place> places = placeRepository.findAll();
        return places.stream()
                .filter(place -> {
                    double distance = getDistance(lat, lon, place.getLatitude(), place.getLongitude());
                    if (distance > place.getRadius()) {
                        return false;
                    } else {
                        place.setDistance(BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP).doubleValue());
                        return true;
                    }
                })
                .sorted(Comparator.comparingDouble(Place::getDistance))
                .collect(Collectors.toList());
    }

    public List<Event> filterEventsInSearchArea(SearchArea searchArea, List<Event> events) {
        //Выбор событий, локации которых попадают в заданную область поиска
        return events.stream()
                .filter(event -> {
                    double distance = getDistance(event.getLat(), event.getLon(), searchArea.getLat(), searchArea.getLon());
                    if (distance > searchArea.getRadius()) {
                        return false;
                    }
                    event.setDistance(BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    return true;
                })
                .sorted(Comparator.comparingDouble(Event::getDistance))
                .collect(Collectors.toList());
    }

    public void checkPlaces(Double lat, Double lon, List<Place> places) {
        if (isNull(places) || places.isEmpty()) {
            return;
        }
        places.forEach(place -> {
            double distance = getDistance(lat, lon, place.getLatitude(), place.getLongitude());
            if (distance > place.getRadius()) {
                log.error("Координаты lat = {} lon = {} не попадают в область покрытия заданных мест", lat, lon);
                throw new ConflictException(String.format("Координаты lat = %f lon = %f не попадают в область покрытия заданных мест", lat, lon),
                        "For the requested operation the conditions are not met.");
            }
        });
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double dist = 0;
        if (lat1 == lat2 && lon1 == lon2) {
            return dist;
        }
        // переводим градусы широты в радианы
        double radLat1 = Math.PI * lat1 / 180;
        double radLat2 = Math.PI * lat2 / 180;
        // находим разность долгот
        double theta = lon1 - lon2;
        // переводим градусы в радианы
        double radTheta = Math.PI * theta / 180;
        // находим длину ортодромии
        dist = Math.sin(radLat1) * Math.sin(radLat2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radTheta);
        if (dist > 1) {
            dist = 1;
        }
        dist = Math.acos(dist);
        // переводим радианы в градусы
        dist = dist * 180 / Math.PI;
        // переводим градусы в метры
        dist = dist * 60 * 1.8524 * 1000;
        return dist;
    }
}
