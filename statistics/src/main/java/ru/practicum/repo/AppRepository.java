package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.App;

public interface AppRepository extends JpaRepository<App, Long> {
    App findByName (String appName);
}
