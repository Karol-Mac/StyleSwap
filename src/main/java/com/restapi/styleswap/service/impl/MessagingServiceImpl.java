package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.repository.MessageRepository;
import com.restapi.styleswap.service.MessageService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.ConversationUtils;
import com.restapi.styleswap.utils.MessageUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MessagingServiceImpl implements MessageService {

    private final ClotheUtils clotheUtils;
    private final MessageUtils messageutils;
    private final MessageRepository messageRepository;
    private final ConversationUtils conversationUtils;

    public MessagingServiceImpl(ClotheUtils clotheUtils, MessageUtils messageutils, MessageRepository messageRepository, ConversationUtils conversationUtils) {
        this.clotheUtils = clotheUtils;
        this.messageutils = messageutils;
        this.messageRepository = messageRepository;
        this.conversationUtils = conversationUtils;
    }

    @Override
    @Transactional
    public void sendMessage(long conversationId, String message, String email) {
        var conversation = conversationUtils.getConversation(conversationId);

        boolean isBuyer = isSenderTheBuyer(email, conversation);

        messageutils.createAndSaveMessage(message, conversation, isBuyer);
    }

    private boolean isSenderTheBuyer(String email, Conversation conversation) {
        if (messageutils.isBuyer(conversation, email))
            return true;
        else if (clotheUtils.isOwner(conversation.getClothe().getId(), email))
            return false;
        else throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
}