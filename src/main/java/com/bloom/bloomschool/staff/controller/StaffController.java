package com.bloom.bloomschool.staff.controller;

import com.bloom.bloomschool.staff.dto.StaffDto;
import com.bloom.bloomschool.staff.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService service;

    @GetMapping("/allstaff")
    public ResponseEntity<List<StaffDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/staff/{uuid}")
    public ResponseEntity<StaffDto> getByUuid(@PathVariable UUID uuid) throws BadRequestException {
        return ResponseEntity.ok(service.getByUuid(uuid));
    }

    @PostMapping("/createStaff")
    public ResponseEntity<?> create(@RequestBody StaffDto req){
        service.create(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/updateStaff/{uuid}")
    public ResponseEntity<?> update(@PathVariable UUID uuid, @RequestBody StaffDto req){
        service.update(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/toggle-status/{uuid}")
    public ResponseEntity<Void> toggleStatus(@PathVariable UUID uuid){
        service.toggleStatus(uuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteStaff/{uuid}")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID uuid){
        service.deleteStaff(uuid);
        return ResponseEntity.noContent().build();
    }

}
