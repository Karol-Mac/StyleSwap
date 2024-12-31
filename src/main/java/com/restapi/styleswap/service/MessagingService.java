package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.MessageDto;

import java.util.List;

public interface MessagingService {
    void startConversation(long clotheId, String email);

    List<ConversationDto> getConversationsBuying(String email);

    List<MessageDto> getMessages(long conversationId, String email);

    List<ConversationDto> getConversationsSelling(long clotheId, String name);

    void sendMessage(long conversationId, String message, String email);
}
