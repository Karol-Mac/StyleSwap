package com.restapi.styleswap.utils;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.ConversationTemplate;
import com.restapi.styleswap.repository.ConversationRepository;
import org.springframework.stereotype.Component;

@Component
public class ConversationUtils {

    private final ConversationRepository conversationRepository;
    private final MessageUtils messageutils;

    public ConversationUtils(ConversationRepository conversationRepository, MessageUtils messageutils) {
        this.conversationRepository = conversationRepository;
        this.messageutils = messageutils;
    }

    public Conversation getConversation(long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
    }

    public void createAndSaveConversation(User buyer, Long clotheId) {
        var conversation = Conversation.builder()
                .buyer(buyer)
                .clothe(new Clothe(clotheId))
                .build();
        conversationRepository.save(conversation);
    }

    public ConversationDto mapToDto(Conversation conversation) {
        var messages = conversation.getMessages().stream()
                .map(messageutils::mapToDto)
                .toList();

        return ConversationDto.builder()
                .buyerId(conversation.getBuyer().getId())
                .clotheId(conversation.getClothe().getId())
                .id(conversation.getId())
                .messages(messages)
                .build();
    }

    public ConversationTemplate mapToTemplate(Conversation conversation) {
        return ConversationTemplate.builder()
                .id(conversation.getId())
                .buyerId(conversation.getBuyer().getId())
                .clotheId(conversation.getClothe().getId())
                .build();
    }
}
