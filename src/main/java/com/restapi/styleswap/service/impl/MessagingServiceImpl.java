package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.entity.Message;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.MessageDto;
import com.restapi.styleswap.repository.ConversationRepository;
import com.restapi.styleswap.repository.MessageRepository;
import com.restapi.styleswap.service.MessagingService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.MessagingUtils;
import com.restapi.styleswap.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MessagingServiceImpl implements MessagingService {

    private final ConversationRepository conversationRepository;
    private final ClotheUtils clotheUtils;
    private final MessagingUtils messagingUtils;
    private final UserUtils userUtils;
    private final MessageRepository messageRepository;

    public MessagingServiceImpl(ConversationRepository conversationRepository,
                                ClotheUtils clotheUtils, MessagingUtils messagingUtils, UserUtils userUtils, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.clotheUtils = clotheUtils;
        this.messagingUtils = messagingUtils;
        this.userUtils = userUtils;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public void startConversation(long clotheId, String email) {
        var clothe = clotheUtils.getClotheFromDB(clotheId);
        var buyer = userUtils.getUser(email);

        validateOwnership(clotheId, email);
        validateClotheAvailability(clothe);
        createAndSaveConversation(buyer, clothe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> getConversationsBuying(String email) {
        return conversationRepository.findByBuyerEmail(email)
                .map(messagingUtils::mapToDto)
                .toList();
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    @Transactional(readOnly = true)
    public List<ConversationDto> getConversationsSelling(long clotheId, String email) {
        return conversationRepository.findByClotheId(clotheId)
                .map(messagingUtils::mapToDto)
                .toList();
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
        boolean isBuyer = defineSenderRole(email, conversation);

        createAndSaveMessage(message, conversation, isBuyer);
    }

    private boolean defineSenderRole(String email, Conversation conversation) {
        if (messagingUtils.isBuyer(conversation, email))
            return true;
        else if (clotheUtils.isOwner(conversation.getClothe().getId(), email))
            return false;
        else throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    private void validateOwnership(long clotheId, String email) {
        if (clotheUtils.isOwner(clotheId, email))
            throw new AccessDeniedException("We don't talk to ourselves");
    }

    private void validateClotheAvailability(Clothe clothe) {
        if (!clothe.isAvailable())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Clothe is not available");
    }

    private void createAndSaveConversation(User buyer, Clothe clothe) {
        var conversation = Conversation.builder()
                .buyer(buyer)
                .clothe(clothe)
                .build();
        conversationRepository.save(conversation);
    }

    private void createAndSaveMessage(String message, Conversation conversation, boolean isBuyer) {
        var messageEntity = Message.builder()
                .conversation(conversation)
                .message(message)
                .isBuyer(isBuyer)
                .build();
        messageRepository.save(messageEntity);
    }

    private boolean isAuthorizedToSeeMessages(String email, Conversation conversation) {
        return messagingUtils.isBuyer(conversation, email) ||
                clotheUtils.isOwner(conversation.getClothe().getId(), email);
    }
}