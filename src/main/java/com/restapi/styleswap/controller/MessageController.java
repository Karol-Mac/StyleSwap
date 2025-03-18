package com.restapi.styleswap.controller;

import com.restapi.styleswap.service.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/conversations")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/{conversationId}/send")
    public ResponseEntity<Void> sendMessage(@PathVariable long conversationId,
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
