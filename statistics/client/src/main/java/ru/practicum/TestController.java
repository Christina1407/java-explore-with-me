package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

//TODO delete
@RestController
@RequestMapping("/")
public class TestController {

    @Autowired
    StatClient statClient;
    @GetMapping
    public void test() {
        statClient.saveHit("test", "test2", "test", LocalDateTime.now());
        statClient.getStats(LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(10), null, false, 1, 20);
    }
}
