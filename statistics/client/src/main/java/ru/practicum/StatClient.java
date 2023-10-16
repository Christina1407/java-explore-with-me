package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class StatClient {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate rest;
    public StatClient(@Value("${stat-service.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();;
    }

    public void saveHit(String uri, String app, String ip, LocalDateTime timestamp) {
        HitDto hitDto = HitDto.builder()
                .uri(uri)
                .app(app)
                .ip(ip)
                .timestamp(timestamp)
                .build();
        ResponseEntity<Object> response = rest.exchange("/hit" , HttpMethod.POST, new HttpEntity<>(hitDto), Object.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(); //TODO сделать ошибку
        }
    }

    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter)
        );
        String url = "/stats?start={start}&end={end}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }

    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "uris", uris
        );
        String url = "/stats?start={start}&end={end}&uris={uris}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }

    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "uris", uris,
                "unique", unique
        );
        String url = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }

    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "unique", unique
        );
        String url = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }

    public List<StatDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique, int page, int size) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "uris", isNull(uris) ? new ArrayList<>() : uris,
                "unique", unique,
                "page", page,
                "size", size
        );
        String url = "/stats?start={start}&end={end}&uris={uris}&unique={unique}&page={page}&size={size}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }


    private ResponseEntity<List<StatDtoResponse>> getStatsResponse(Map<String, Object> parameters, String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<StatDtoResponse>> response = rest.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers),  new ParameterizedTypeReference<List<StatDtoResponse>>() {
        }, parameters);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(); // TODO сделать ошибку
        }
        return response;
    }


}
