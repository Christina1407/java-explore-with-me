package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.validation.ValidationException;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
@Slf4j
@Validated
public class StatClient {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestTemplate rest;


    public StatClient(@Value("${stat-service.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        ;
    }

    public void saveHit(@NotBlank @Size(max = 2000, message = "uri is more than 2000 symbols") String uri,
                        @NotBlank @Size(max = 200, message = "appName is more than 200 symbols") String app,
                        @NotBlank @Size(max = 50, message = "ip is more than 50 symbols")
                        @Pattern(regexp = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$", message = "Invalid IP Address") String ip,
                        @NotNull @PastOrPresent(message = "timestamp is null or in future") LocalDateTime timestamp) {
        HitDto hitDto = HitDto.builder()
                .uri(uri)
                .app(app)
                .ip(ip)
                .timestamp(timestamp)
                .build();
        ResponseEntity<Object> response = rest.exchange("/hit", HttpMethod.POST, new HttpEntity<>(hitDto), Object.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Ошибка при сохранении просмотра {}", hitDto);
            throw new StatsException("Ошибка при сохранении просмотра " + response.getBody());
        }
    }

    public List<StatDtoResponse> getStats(@NotNull @PastOrPresent(message = "start can't be in future") LocalDateTime start,
                                          @NotNull LocalDateTime end) {
        validate(start, end);
        log.info("Попытка получение статистики от сервиса статистики с параметрами start = {} , end = {}", start, end);
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter)
        );
        String url = "/stats?start={start}&end={end}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }

    public List<StatDtoResponse> getStats(@NotNull @PastOrPresent(message = "start can't be in future") LocalDateTime start,
                                          @NotNull LocalDateTime end,
                                          List<String> uris) {
        validate(start, end);
        log.info("Попытка получение статистики от сервиса статистики с параметрами start = {} , end = {}, uris = {}", start, end, uris);
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "uris", isNull(uris) ? new ArrayList<>() : String.join(",", uris)
        );
        String url = "/stats?start={start}&end={end}&uris={uris}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }

    public List<StatDtoResponse> getStats(@NotNull @PastOrPresent(message = "start can't be in future") LocalDateTime start,
                                          @NotNull LocalDateTime end,
                                          List<String> uris,
                                          boolean unique) {
        validate(start, end);
        log.info("Попытка получение статистики от сервиса статистики с параметрами start = {} , end = {}, uris = {}, unique = {}", start, end, uris, unique);
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "uris", isNull(uris) ? new ArrayList<>() : String.join(",", uris),
                "unique", unique
        );
        String url = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }

    public List<StatDtoResponse> getStats(@NotNull @PastOrPresent(message = "start can't be in future") LocalDateTime start,
                                          @NotNull LocalDateTime end,
                                          boolean unique) {
        validate(start, end);
        log.info("Попытка получение статистики от сервиса статистики с параметрами start = {} , end = {}, unique = {}", start, end, unique);
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "unique", unique
        );
        String url = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<List<StatDtoResponse>> response = getStatsResponse(parameters, url);
        return response.getBody();
    }

    public List<StatDtoResponse> getStats(@NotNull @PastOrPresent(message = "start can't be in future") LocalDateTime start,
                                          @NotNull LocalDateTime end,
                                          List<String> uris,
                                          Boolean unique,
                                          @Min(0) int page,
                                          @Min(1) int size) {
        validate(start, end);
        log.info("Попытка получение статистики от сервиса статистики с параметрами start = {} , end = {}, uris = {}, unique = {}, page = {}, size = {}",
                start, end, uris, unique, page, size);
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "uris", isNull(uris) ? new ArrayList<>() : String.join(",", uris),
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
                url, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<StatDtoResponse>>() {
                }, parameters);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Ошибка при получении статистики {}", parameters);
            throw new StatsException("Ошибка при получении статистики " + response.getBody());
        }
        return response;
    }

    private void validate(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Start must be before end");
        }
    }
}
