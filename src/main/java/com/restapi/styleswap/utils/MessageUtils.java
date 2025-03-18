package com.restapi.styleswap.utils;

import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.entity.Message;
import com.restapi.styleswap.payload.MessageDto;
import com.restapi.styleswap.repository.ConversationRepository;
import com.restapi.styleswap.repository.MessageRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageUtils {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public MessageUtils(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public void createAndSaveMessage(String content, Conversation conversation, boolean isBuyer) {
        var messageEntity = Message.builder()
                .content(content)
                .ifFromBuyer(isBuyer)
                .build();
        var savedMessage = messageRepository.save(messageEntity);

        conversation.getMessages().add(savedMessage);
        conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public boolean isBuyer(Conversation conversation, String email) {
        return conversationRepository.existsByIdAndBuyerEmail(conversation.getId(), email);
    }

    public MessageDto mapToDto(Message message) {
        return MessageDto.builder()
                .ifFromBuyer(message.getIfFromBuyer())
                .messageContent(message.getContent())
                .build();
    }
}