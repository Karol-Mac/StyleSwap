package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.service.ConversationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@SecurityRequirement(name = "bearerAuth")
public class ConversationController {
    private final ConversationService conversationService;


    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    public ResponseEntity<Void> startConversation(@RequestParam long clotheId, Principal principal) {

        conversationService.startConversation(clotheId, principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/buying")
    public ResponseEntity<List<ConversationDto>> getConversationsBuying(Principal principal) {
        return ResponseEntity.ok(conversationService.getConversationsBuying(principal.getName()));
    }

    @GetMapping("/selling")
    public ResponseEntity<List<ConversationDto>> getConversationsSelling(@RequestParam long clotheId, Principal principal) {
        return ResponseEntity.ok(conversationService.getConversationsSelling(clotheId, principal.getName()));
    }
}