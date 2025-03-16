package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.ConversationDto;

import java.util.List;

public interface ConversationService {
    void startConversation(long clotheId, String email);

    List<ConversationDto> getConversationsBuying(String email);

    List<ConversationDto> getConversationsSelling(long clotheId, String email);


}
