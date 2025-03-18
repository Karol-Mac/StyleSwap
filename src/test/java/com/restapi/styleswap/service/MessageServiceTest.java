//package com.restapi.styleswap.service;
//
//import com.restapi.styleswap.entity.Clothe;
//import com.restapi.styleswap.entity.Conversation;
//import com.restapi.styleswap.entity.Message;
//import com.restapi.styleswap.entity.User;
//import com.restapi.styleswap.exception.ApiException;
//import com.restapi.styleswap.payload.ConversationDto;
//import com.restapi.styleswap.payload.MessageDto;
//import com.restapi.styleswap.repository.ConversationRepository;
//import com.restapi.styleswap.repository.MessageRepository;
//import com.restapi.styleswap.service.impl.MessagingServiceImpl;
//import com.restapi.styleswap.utils.ClotheUtils;
//import com.restapi.styleswap.utils.MessageUtils;
//import com.restapi.styleswap.utils.UserUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
//
//import java.util.List;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.when;
//
//public class MessageServiceTest {
//    @Mock
//    private ConversationRepository conversationRepository;
//
//    @Mock
//    private ClotheUtils clotheUtils;
//
//    @Mock
//    private MessageUtils messagingUtils;
//
//    @Mock
//    private UserUtils userUtils;
//
//    @Mock
//    private MessageRepository messageRepository;
//
//    @Mock
//    private Clothe clothe;
//
//    @Mock
//    private User buyer;
//
//    @Mock
//    private User seller;
//
//    @Mock
//    private Conversation conversation;
//
//    @InjectMocks
//    private MessagingServiceImpl messagingService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void startConversation_createsConversationSuccessfully() {
//
//        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
//        when(userUtils.getUser("user@example.com")).thenReturn(buyer);
//        when(clothe.getUser()).thenReturn(seller);
//        when(clothe.getUser().getId()).thenReturn(2L);
//        when(buyer.getId()).thenReturn(3L);
//        when(clothe.isAvailable()).thenReturn(true);
//
//        messagingService.startConversation(1L, "user@example.com");
//
//        verify(conversationRepository, times(1)).save(any(Conversation.class));
//    }
//
//    @Test
//    void startConversation_throwsAccessDeniedExceptionWhenTalkingToSelf() {
//
//        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
//        when(userUtils.getUser("user@example.com")).thenReturn(buyer);
//        when(clotheUtils.isOwner(1L, "user@example.com")).thenReturn(true);
//
//        AccessDeniedException exception =  assertThrows(AccessDeniedException.class,
//                () -> messagingService.startConversation(1L, "user@example.com"));
//
//        assertEquals("We don't talk to ourselves", exception.getMessage());
//        verify(conversationRepository, never()).save(any(Conversation.class));
//    }
//
//    @Test
//    void startConversation_throwsApiExceptionWhenClotheNotAvailable() {
//
//        when(clotheUtils.getClotheFromDB(1L)).thenReturn(clothe);
//        when(userUtils.getUser("user@example.com")).thenReturn(buyer);
//        when(clothe.getUser()).thenReturn(seller);
//        when(clothe.getUser().getId()).thenReturn(2L);
//        when(clothe.isAvailable()).thenReturn(false);
//
//        ApiException exception = assertThrows(ApiException.class,
//                () -> messagingService.startConversation(1L, "user@example.com"));
//
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
//        assertEquals("Clothe is not available", exception.getMessage());
//    }
//
//    @Test
//    void getConversationsBuying_returnsConversationsSuccessfully() {
//        List<ConversationDto> conversationDtos = List.of(mock(ConversationDto.class));
//
//        when(conversationRepository.findByBuyerEmail("user@example.com")).thenReturn(Stream.of(conversation));
//        when(messagingUtils.mapToDto(conversation)).thenReturn(conversationDtos.get(0));
//
//        List<ConversationDto> result = messagingService.getConversationsBuying("user@example.com");
//
//        assertEquals(conversationDtos, result);
//    }
//
//    @Test
//    void getConversationsSelling_returnsConversationsSuccessfully() {
//        List<ConversationDto> conversationDtos = List.of(mock(ConversationDto.class));
//
//        when(conversationRepository.findByClotheId(1L)).thenReturn(Stream.of(conversation));
//        when(messagingUtils.mapToDto(conversation)).thenReturn(conversationDtos.get(0));
//
//        List<ConversationDto> result = messagingService.getConversationsSelling(1L, "user@example.com");
//
//        assertEquals(conversationDtos, result);
//    }
//
//    @Test
//    void getMessages_returnsMessagesSuccessfully() {
//        User currentUser = mock(User.class);
//        List<MessageDto> messageDtos = List.of(mock(MessageDto.class));
//        String email = "user@example.com";
//
//        when(messagingUtils.getConversation(1L)).thenReturn(conversation);
//        when(messagingUtils.isBuyer(conversation, email)).thenReturn(true);
//        when(conversation.getMessages()).thenReturn(List.of(mock(Message.class)));
//        when(messagingUtils.mapToDto(any(Message.class))).thenReturn(messageDtos.get(0));
//
//        List<MessageDto> result = messagingService.getMessages(1L, email);
//
//        assertEquals(messageDtos, result);
//    }
//
//    @Test
//    void getMessages_throwsAccessDeniedExceptionWhenUnauthorized() {
//        User currentUser = mock(User.class);
//
//        when(messagingUtils.getConversation(1L)).thenReturn(conversation);
//        when(userUtils.getUser("user@example.com")).thenReturn(currentUser);
//        when(conversation.getBuyer()).thenReturn(buyer);
//        when(conversation.getBuyer().getId()).thenReturn(2L);
//        when(currentUser.getId()).thenReturn(1L);
//        when(conversation.getClothe()).thenReturn(clothe);
//        when(clotheUtils.isOwner(conversation.getClothe().getId(), "user@example.com")).thenReturn(false);
//
//        assertThrows(AccessDeniedException.class, () -> messagingService.getMessages(1L, "user@example.com"));
//    }
//
//    @Test
//    void sendMessage_sendsMessageSuccessfully() {
//        Message messageEntity = mock(Message.class);
//
//        when(messagingUtils.getConversation(1L)).thenReturn(conversation);
//        when(messagingUtils.isBuyer(conversation, "user@example.com")).thenReturn(true);
//
//        messagingService.sendMessage(1L, "Hello", "user@example.com");
//
//        verify(messageRepository, times(1)).save(any(Message.class));
//    }
//
//    @Test
//    void sendMessage_throwsApiExceptionWhenUnauthorized() {
//
//        when(messagingUtils.getConversation(1L)).thenReturn(conversation);
//        when(messagingUtils.isBuyer(conversation, "user@example.com")).thenReturn(false);
//        when(conversation.getClothe()).thenReturn(clothe);
//        when(clotheUtils.isOwner(conversation.getClothe().getId(), "user@example.com")).thenReturn(false);
//
//        ApiException exception = assertThrows(ApiException.class, () -> messagingService.sendMessage(1L, "Hello", "user@example.com"));
//        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
//        assertEquals("Unauthorized", exception.getMessage());
//    }
//}