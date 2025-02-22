package aifriend.ai_backend.controller;

import aifriend.ai_backend.dto.MessageRequest;
import aifriend.ai_backend.dto.MessageResponse;
import aifriend.ai_backend.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*") // Allow cross-origin requests for frontend
public class MessageController {

    
    @Autowired
    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    

    @PostMapping
    public MessageResponse sendMessage(@RequestBody MessageRequest request) {
        return messageService.generateResponse(request);
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("AI Backend is running!");
    }
}