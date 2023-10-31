package ru.practicum.aspect;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.practicum.StatClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
@Aspect
@Slf4j
@AllArgsConstructor
public class StatAspect {
    private HttpServletRequest request;
    private StatClient statClient;

    @Pointcut("@annotation(SaveStatistic)")
    public void stringProcessingMethods() {
    }

    @AfterReturning(pointcut = "@annotation(SaveStatistic)")
    public void logAfterReturning() {
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String app = "ewm";
        statClient.saveHit(uri, app, ip, LocalDateTime.now());
        log.info("Статистика успешно сохранена");
    }
}
