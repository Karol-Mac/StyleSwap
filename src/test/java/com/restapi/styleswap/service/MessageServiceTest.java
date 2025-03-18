package com.restapi.styleswap.service;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.service.impl.MessagingServiceImpl;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.ConversationUtils;
import com.restapi.styleswap.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class MessageServiceTest {

    @Mock
    private ClotheUtils clotheUtils;

    @Mock
    private MessageUtils messageutils;

    @Mock
    private ConversationUtils conversationUtils;

    @Mock
    private Conversation conversation;

    @Mock
    private Clothe clothe;

    @InjectMocks
    private MessagingServiceImpl messagingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendMessage_sendsMessageSuccessfully() {

        when(conversationUtils.getConversation(1L)).thenReturn(conversation);
        when(messageutils.isBuyer(0L, "user@example.com")).thenReturn(true);
        when(conversation.getClothe()).thenReturn(clothe);

        messagingService.sendMessage(1L, "Hello", "user@example.com");

        verify(messageutils, times(1)).createAndSaveMessage("Hello", conversation, true);
    }

    @Test
    void sendMessage_throwsAccessDeniedExceptionWhenUnauthorized() {

        when(conversationUtils.getConversation(1L)).thenReturn(conversation);
        when(messageutils.isBuyer(1L, "user@example.com")).thenReturn(false);
        when(conversation.getClothe()).thenReturn(clothe);
        when(clotheUtils.isOwner(0L, "user@example.com")).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                                () -> messagingService.sendMessage(1L, "Hello", "user@example.com"));

        assertEquals("You don't have permission to send this message", exception.getMessage());
    }

    @Test
    void sendMessage_sendsMessageAsOwnerSuccessfully() {

        when(conversationUtils.getConversation(1L)).thenReturn(conversation);
        when(messageutils.isBuyer(1L, "user@example.com")).thenReturn(false);
        when(conversation.getClothe()).thenReturn(clothe);
        when(clotheUtils.isOwner(0L, "user@example.com")).thenReturn(true);

        messagingService.sendMessage(1L, "Hello", "user@example.com");

        verify(messageutils, times(1)).createAndSaveMessage("Hello", conversation, false);
    }
}
