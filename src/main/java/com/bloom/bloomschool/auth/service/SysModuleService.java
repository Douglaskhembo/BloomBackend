package com.bloom.bloomschool.auth.service;

import com.bloom.bloomschool.auth.dto.Requests.ModuleRequest;
import com.bloom.bloomschool.auth.model.SysModule;
import com.bloom.bloomschool.auth.repo.SysModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysModuleService {

    private final SysModuleRepository moduleRepository;

    public List<SysModule> getAll() { return moduleRepository.findAll(); }

    public SysModule getByUuid(UUID uuid) {
        return moduleRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Module not found"));
    }

    @Transactional
    public SysModule create(ModuleRequest request) {
        if (moduleRepository.existsByModuleName(request.getModuleName()))
            throw new IllegalArgumentException("Module already exists");
        return moduleRepository.save(SysModule.builder().moduleName(request.getModuleName()).build());
    }

    @Transactional
    public SysModule update(UUID uuid, ModuleRequest request) {
        SysModule m = getByUuid(uuid);
        m.setModuleName(request.getModuleName());
        return moduleRepository.save(m);
    }

    @Transactional
    public void delete(UUID uuid) {
        moduleRepository.delete(getByUuid(uuid));
    }
}
