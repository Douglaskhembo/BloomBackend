package com.bloom.bloomschool.communication.controller;

import com.bloom.bloomschool.communication.dto.MessageRequestDTO;
import com.bloom.bloomschool.communication.dto.MessageResponseDTO;
import com.bloom.bloomschool.communication.service.MessageService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/message")
public class MessageController {
    private final MessageService messageService;

    //Get All Messages
   @GetMapping
    public ResponseEntity<List<MessageResponseDTO>> getAllMessages(){
       return ResponseEntity.ok(messageService.getAllMessages());
   }

   //Get Message by UUID
   @GetMapping("/{uuid}")
    public ResponseEntity<MessageResponseDTO> getMessageByUuid(@PathVariable UUID uuid){
       return ResponseEntity.ok(messageService.getMessageByUuid(uuid));
   }

   //Create Message
    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody MessageRequestDTO req){
       messageService.sendMessage(req);
       return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //Update Message
    @PutMapping("/{uuid}")
    public ResponseEntity<?> updateMessage(@PathVariable UUID uuid, @RequestBody MessageRequestDTO req){
       messageService.updateMessage(uuid, req);
       return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //Delete Message
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteMessage(@PathVariable UUID uuid){
       messageService.deleteMessage(uuid);
       return ResponseEntity.noContent().build();
    }
}
