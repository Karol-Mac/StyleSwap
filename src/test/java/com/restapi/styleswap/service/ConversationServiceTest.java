package com.restapi.styleswap.service;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.ConversationTemplate;
import com.restapi.styleswap.repository.ConversationRepository;
import com.restapi.styleswap.service.impl.ConversationServiceImpl;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.ConversationUtils;
import com.restapi.styleswap.utils.MessageUtils;
import com.restapi.styleswap.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ClotheUtils clotheUtils;

    @Mock
    private UserUtils userUtils;

    @Mock
    private MessageUtils messageutils;

    @Mock
    private ConversationUtils conversationUtils;

    @Mock
    private Conversation conversation;

    @Mock
    private ConversationTemplate conversationTemplate;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startConversation_createsConversationSuccessfully() {
        Clothe clothe = mock(Clothe.class);
        User buyer = mock(User.class);

        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
        when(userUtils.getUser("user@example.com")).thenReturn(buyer);
        when(clothe.isAvailable()).thenReturn(true);
        doNothing().when(conversationUtils).createAndSaveConversation(buyer, 1L);

        conversationService.startConversation(1L, "user@example.com");

        verify(conversationUtils, times(1)).createAndSaveConversation(buyer, 1L);
    }

    @Test
    void getConversationsBuying_returnsConversationsSuccessfully() {

        when(conversationRepository.findByBuyerEmail("user@example.com")).thenReturn(Stream.of(conversation));
        when(conversationUtils.mapToTemplate(conversation)).thenReturn(conversationTemplate);

        List<ConversationTemplate> result = conversationService.getConversationsBuying("user@example.com");

        assertEquals(List.of(conversationTemplate), result);
    }

    @Test
    void getConversationsSelling_returnsConversationsSuccessfully() {

        when(conversationRepository.findByClotheId(1L)).thenReturn(Stream.of(conversation));
        when(conversationUtils.mapToTemplate(conversation)).thenReturn(conversationTemplate);

        List<ConversationTemplate> result = conversationService.getConversationsSelling(1L, "user@example.com");

        assertEquals(List.of(conversationTemplate), result);
    }

    @Test
    void getConversation_returnsConversationSuccessfully() {
        ConversationDto conversationDto = mock(ConversationDto.class);
        Clothe clothe = mock(Clothe.class);

        when(conversationUtils.getConversation(1L)).thenReturn(conversation);
        when(conversation.getClothe()).thenReturn(clothe);
        when(messageutils.isBuyer(1L, "user@example.com")).thenReturn(true);
        when(clotheUtils.isOwner(0L, "user@example.com")).thenReturn(true);
        when(conversationUtils.mapToDto(conversation)).thenReturn(conversationDto);

        ConversationDto result = conversationService.getConversation(1L, "user@example.com");

        assertEquals(conversationDto, result);
    }
}