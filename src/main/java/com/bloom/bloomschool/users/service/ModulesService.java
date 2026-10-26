package com.bloom.bloomschool.users.service;

import com.bloom.bloomschool.student.exceptions.ResourceNotFoundException;
import com.bloom.bloomschool.users.dto.ModulesRequestDTO;
import com.bloom.bloomschool.users.dto.ModulesResponseDTO;
import com.bloom.bloomschool.users.entity.Modules;
import com.bloom.bloomschool.users.repository.ModulesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ModulesService {
    private final ModulesRepository moduleRepo;

    private Modules findEntity(UUID uuid){
        return moduleRepo.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Module Not Found"));
    }
    private Modules findEntity(String name){
        return moduleRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Module Not Found"));
    }

    public List<ModulesResponseDTO> getAllModules(){
        return moduleRepo.findAll().stream()
                .map(m -> ModulesResponseDTO.builder()
                        .uuid(m.getUuid())
                        .name(m.getName())
                        .build())
                .toList();
    }

    public ModulesResponseDTO getModuleByUuid(UUID uuid){
        Modules m = findEntity(uuid);

        return ModulesResponseDTO.builder()
                .uuid(m.getUuid())
                .name(m.getName())
                .build();
    }
    public ModulesResponseDTO getModuleByName(String name){
        Modules m = findEntity(name);

        return ModulesResponseDTO.builder()
                .uuid(m.getUuid())
                .name(m.getName())
                .build();
    }

    @Transactional
    public void createModule(ModulesRequestDTO req){
        if(moduleRepo.existsByName(req.getName())){
            throw new RuntimeException("Module already exists");
        }
        moduleRepo.save(Modules.builder()
                .name(req.getName())
                .build());
    }

    @Transactional
    public void updateModule(UUID uuid, ModulesRequestDTO req){
        Modules m = findEntity(uuid);

        m.setName(req.getName());

        moduleRepo.save(m);
    }
    @Transactional
    public void deleteModule(UUID uuid){
        Modules m = findEntity(uuid);
        moduleRepo.delete(m);
    }
}
