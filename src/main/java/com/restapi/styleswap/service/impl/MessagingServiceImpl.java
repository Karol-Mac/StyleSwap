package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.entity.Message;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.payload.MessageDto;
import com.restapi.styleswap.repository.MessageRepository;
import com.restapi.styleswap.service.MessageService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.MessagingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MessagingServiceImpl implements MessageService {

    private final ClotheUtils clotheUtils;
    private final MessagingUtils messagingUtils;
    private final MessageRepository messageRepository;

    public MessagingServiceImpl(ClotheUtils clotheUtils, MessagingUtils messagingUtils,MessageRepository messageRepository) {
        this.clotheUtils = clotheUtils;
        this.messagingUtils = messagingUtils;
        this.messageRepository = messageRepository;
    }

    @Override
    @PreAuthorize("@userRepository.existsByEmail(#email)")
    public List<MessageDto> getMessages(long conversationId, String email) {
        var conversation = messagingUtils.getConversation(conversationId);

        if (!isAuthorizedToSeeMessages(email, conversation))
            throw new AccessDeniedException("You don't have permission to see this message");

        return conversation.getMessages().stream().map(messagingUtils::mapToDto).toList();
    }

    @Override
    @Transactional
    public void sendMessage(long conversationId, String message, String email) {
        var conversation = messagingUtils.getConversation(conversationId);
        boolean isBuyer = isSenderTheBuyer(email, conversation);

        createAndSaveMessage(message, conversation, isBuyer);
    }

    private boolean isSenderTheBuyer(String email, Conversation conversation) {
        if (messagingUtils.isBuyer(conversation, email))
            return true;
        else if (clotheUtils.isOwner(conversation.getClothe().getId(), email))
            return false;
        else throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    private void createAndSaveMessage(String message, Conversation conversation, boolean isBuyer) {
        var messageEntity = Message.builder()
                .conversation(conversation)
                .message(message)
                .ifFromBuyer(isBuyer)
                .build();
        messageRepository.save(messageEntity);
    }

    private boolean isAuthorizedToSeeMessages(String email, Conversation conversation) {
        return messagingUtils.isBuyer(conversation, email) ||
                clotheUtils.isOwner(conversation.getClothe().getId(), email);
    }
}