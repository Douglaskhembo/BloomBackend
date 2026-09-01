package com.bloom.bloomschool.calender.controller;

import com.bloom.bloomschool.calender.dto.TermPeriodRequestDTO;
import com.bloom.bloomschool.calender.dto.TermPeriodResponseDTO;
import com.bloom.bloomschool.calender.service.TermPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/calender/periods")
@RequiredArgsConstructor
public class TermPeriodController {
    private final TermPeriodService termService;

    @GetMapping
    public ResponseEntity<List<TermPeriodResponseDTO>> getTermPeriods(){
        return ResponseEntity.ok(termService.getAllTermPeriods());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TermPeriodResponseDTO> getTermPeriodByUuid(@PathVariable UUID uuid){
        return ResponseEntity.ok(termService.findTermPeriodByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<?> createTermPeriod(@RequestBody TermPeriodRequestDTO request) {
        termService.createTermPeriod(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody TermPeriodRequestDTO req) {
        termService.updateTermPeriod(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteTermPeriod(@PathVariable UUID uuid) {
        termService.deleteTermPeriod(uuid);
        return ResponseEntity.noContent().build();
    }
}
