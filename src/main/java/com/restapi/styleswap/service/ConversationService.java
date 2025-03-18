package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.ConversationTemplate;

import java.util.List;

public interface ConversationService {
    void startConversation(long clotheId, String email);

    List<ConversationTemplate> getConversationsBuying(String email);

    List<ConversationTemplate> getConversationsSelling(long clotheId, String email);

    ConversationDto getConversation(long clotheId, String email);
}
