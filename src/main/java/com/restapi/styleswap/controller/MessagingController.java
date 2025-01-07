package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.MessageDto;
import com.restapi.styleswap.service.MessagingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@SecurityRequirement(name = "bearerAuth")
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
    public ResponseEntity<List<MessageDto>> getMessages(@RequestParam long conversationId, Principal principal) {
        return ResponseEntity.ok(messagingService.getMessages(conversationId, principal.getName()));
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestParam long conversationId,
                                            @RequestBody String message,
                                            Principal principal) {

        messagingService.sendMessage(conversationId, message, principal.getName());
        return ResponseEntity.created(getLocation(conversationId)).build();
    }

    public URI getLocation(Object resourceId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/api/conversations")
                .replaceQueryParam("conversationId", resourceId)
                .build()
                .toUri();
    }
}