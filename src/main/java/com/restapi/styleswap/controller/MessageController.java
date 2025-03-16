package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.MessageDto;
import com.restapi.styleswap.service.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(@RequestParam long conversationId, Principal principal) {
        return ResponseEntity.ok(messageService.getMessages(conversationId, principal.getName()));
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestParam long conversationId,
                                            @RequestBody String message,
                                            Principal principal) {

        messageService.sendMessage(conversationId, message, principal.getName());
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
