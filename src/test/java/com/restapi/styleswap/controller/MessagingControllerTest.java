package com.restapi.styleswap.controller;

import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.MessageDto;
import com.restapi.styleswap.service.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessagingControllerTest {

    @Mock
    private MessagingService messagingService;

    @Mock
    private Principal principal;

    @InjectMocks
    private MessagingController messagingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startConversationReturnsCreatedStatus() {
        when(principal.getName()).thenReturn("user");

        ResponseEntity<Void> response = messagingController.startConversation(1L, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(messagingService, times(1)).startConversation(1L, "user");
    }

    @Test
    void getConversationsBuyingReturnsListOfConversations() {
        when(principal.getName()).thenReturn("user");
        List<ConversationDto> conversations = List.of(new ConversationDto());
        when(messagingService.getConversationsBuying("user")).thenReturn(conversations);

        ResponseEntity<List<ConversationDto>> response = messagingController.getConversationsBuying(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(conversations, response.getBody());
    }

    @Test
    void getConversationsSellingReturnsListOfConversations() {
        when(principal.getName()).thenReturn("user");
        List<ConversationDto> conversations = List.of(new ConversationDto());
        when(messagingService.getConversationsSelling(1L, "user")).thenReturn(conversations);

        ResponseEntity<List<ConversationDto>> response = messagingController.getConversationsSelling(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(conversations, response.getBody());
    }

    @Test
    void getMessagesReturnsListOfMessages() {
        when(principal.getName()).thenReturn("user");
        List<MessageDto> messages = List.of(new MessageDto());
        when(messagingService.getMessages(1L, "user")).thenReturn(messages);

        ResponseEntity<List<MessageDto>> response = messagingController.getMessages(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages, response.getBody());
    }
}