package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatClient {
    private final RestTemplate rest;
    @Value("${stat-service.url}")
    String serverUrl;

    public void saveHit(String uri, String app, String ip, LocalDateTime timestamp) {
        HitDto hitDto = HitDto.builder()
                .uri(uri)
                .app(app)
                .ip(ip)
                .timestamp(timestamp)
                .build();
        ResponseEntity<Object> exchange = rest.exchange(serverUrl + "/hit", HttpMethod.POST, new HttpEntity<>(hitDto), Object.class);
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(); //TODO сделать ошибку
        }
    }


}
