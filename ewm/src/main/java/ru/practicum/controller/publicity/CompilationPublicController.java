package ru.practicum.controller.publicity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.service.CompilationService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping("/{compilationId}")
    public CompilationDto findCompilationById(@PathVariable @Min(1) Long compilationId) {
        log.info("Get compilation id = {}", compilationId);
        return compilationService.findCompilationById(compilationId);
    }

    @GetMapping
    public List<CompilationDto> findCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Get compilations pinned = {}", pinned);
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationService.findCompilations(pinned, pageable);
    }
}
