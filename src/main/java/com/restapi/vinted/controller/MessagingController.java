package com.restapi.vinted.controller;

import com.restapi.vinted.payload.ConversationDto;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.service.MessagingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class MessagingController {
    private final MessagingService messagingService;


    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping
    public ResponseEntity<Void> startConversation(@RequestParam long clotheId, Principal principal) {

        messagingService.startConversation(clotheId, principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/buying")
    public ResponseEntity<List<ConversationDto>> getConversationsBuying(Principal principal) {
        return ResponseEntity.ok(messagingService.getConversationsBuying(principal.getName()));
    }

    @GetMapping("/selling")
    public ResponseEntity<List<ConversationDto>> getConversationsSelling(@RequestParam long clotheId, Principal principal) {
        return ResponseEntity.ok(messagingService.getConversationsSelling(clotheId, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(@RequestParam long buyerId, @RequestParam long clotheId, Principal principal) {
        return ResponseEntity.ok(messagingService.getMessages(buyerId, clotheId, principal.getName()));
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestParam long conversationId,
                                            @RequestBody String message,
                                            Principal principal) {

        messagingService.sendMessage(conversationId, message, principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}