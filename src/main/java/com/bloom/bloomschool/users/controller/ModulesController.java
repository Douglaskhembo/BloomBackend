package com.bloom.bloomschool.users.controller;

import com.bloom.bloomschool.users.dto.ModulesRequestDTO;
import com.bloom.bloomschool.users.dto.ModulesResponseDTO;
import com.bloom.bloomschool.users.service.ModulesService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/modules")

public class ModulesController {
    private final ModulesService modService;

    @GetMapping
    public ResponseEntity<List<ModulesResponseDTO>> getAllModules(){
        return ResponseEntity.ok(modService.getAllModules());
    }
    @GetMapping("/{uuid}")
    public ResponseEntity<ModulesResponseDTO> getModulesByUuid(@PathVariable UUID uuid){
        return ResponseEntity.ok(modService.getModuleByUuid(uuid));
    }
    @GetMapping("/name")
    public ResponseEntity<ModulesResponseDTO> getModuleByName(@RequestParam String name){
        return ResponseEntity.ok(modService.getModuleByName(name));

    }

    @PostMapping
    public ResponseEntity<?> createModule(@RequestBody ModulesRequestDTO req){
        modService.createModule(req);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateModule(@PathVariable UUID uuid, @RequestBody ModulesRequestDTO req){
        modService.updateModule(uuid, req);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteModules(@PathVariable UUID uuid){
        modService.deleteModule(uuid);
        return ResponseEntity.noContent().build();
    }
}
