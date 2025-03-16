package com.restapi.styleswap.utils;

import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.entity.Message;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.MessageDto;
import com.restapi.styleswap.repository.ConversationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessagingUtils {

    private final ConversationRepository conversationRepository;

    public MessagingUtils(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Conversation getConversation(long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
    }

    @Transactional(readOnly = true)
    public boolean isBuyer(Conversation conversation, String email) {
        return conversationRepository.findByIdAndBuyerEmail(conversation.getId(), email).isPresent();
    }

    public MessageDto mapToDto(Message message) {
        return MessageDto.builder()
                .ifFromBuyer(message.isIfFromBuyer())
                .clotheId(message.getConversation().getClothe().getId())
                .buyerId(message.getConversation().getBuyer().getId())
                .messageContent(message.getMessage())
                .build();
    }
}