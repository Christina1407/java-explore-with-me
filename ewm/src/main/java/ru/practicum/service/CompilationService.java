package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compilationId);

    CompilationDto findCompilationById(Long compilationId);

    List<CompilationDto> findCompilations(Boolean pinned, Pageable pageable);
}
