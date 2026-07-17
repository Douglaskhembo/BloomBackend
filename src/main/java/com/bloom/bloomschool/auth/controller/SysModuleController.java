package com.bloom.bloomschool.auth.controller;

import com.bloom.bloomschool.auth.dto.Requests.ModuleRequest;
import com.bloom.bloomschool.auth.service.SysModuleService;
import com.bloom.bloomschool.auth.utils.ApiResponse;
import com.bloom.bloomschool.auth.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
public class SysModuleController {

    private final SysModuleService moduleService;
    private final GenericResponse genericResponse;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAll() {
        return genericResponse.response(moduleService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> getByUuid(@PathVariable UUID uuid) {
        return genericResponse.response(moduleService.getByUuid(uuid), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody ModuleRequest request) {
        return genericResponse.response(moduleService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable UUID uuid,
                                                      @RequestBody ModuleRequest request) {
        return genericResponse.response(moduleService.update(uuid, request), HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable UUID uuid) {
        moduleService.delete(uuid);
        return genericResponse.response(null, HttpStatus.OK);
    }
}
