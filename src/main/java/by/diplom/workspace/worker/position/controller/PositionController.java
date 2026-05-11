package by.diplom.workspace.worker.position.controller;

import by.diplom.workspace.worker.position.dto.request.create.CreatePositionRequestDto;
import by.diplom.workspace.worker.position.dto.request.update.UpdatePositionRequestDto;
import by.diplom.workspace.worker.position.dto.response.PositionResponseDto;
import by.diplom.workspace.worker.position.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/positions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PositionController {
    private final PositionService positionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PositionResponseDto createPosition(
            @Valid @RequestBody CreatePositionRequestDto request) {
        return positionService.createPosition(request);
    }

    @GetMapping
    public List<PositionResponseDto> getAllPositions() {
        return positionService.getAllPositions();
    }

    @PutMapping("/{id}")
    public PositionResponseDto updatePosition(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePositionRequestDto request) {
        return positionService.updatePosition(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
    }

    @GetMapping("{id}")
    public PositionResponseDto getPosition(@PathVariable Long id) {
        return positionService.getPosition(id);
    }
}
