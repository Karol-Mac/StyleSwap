package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.ConversationTemplate;
import com.restapi.styleswap.repository.ConversationRepository;
import com.restapi.styleswap.service.ConversationService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.ConversationUtils;
import com.restapi.styleswap.utils.MessageUtils;
import com.restapi.styleswap.utils.UserUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ClotheUtils clotheUtils;
    private final UserUtils userUtils;
    private final MessageUtils messageutils;
    private final ConversationUtils conversationUtils;

    public ConversationServiceImpl(ConversationRepository conversationRepository, ClotheUtils clotheUtils,
                                   UserUtils userUtils, MessageUtils messageutils, ConversationUtils conversationUtils) {
        this.conversationRepository = conversationRepository;
        this.clotheUtils = clotheUtils;
        this.userUtils = userUtils;
        this.messageutils = messageutils;
        this.conversationUtils = conversationUtils;
    }

    @Override
    @Transactional
    public void startConversation(long clotheId, String email) {
        var clothe = clotheUtils.getClotheFromDB(clotheId);
        var buyer = userUtils.getUser(email);

        validateOwnership(clotheId, email);
        clotheUtils.validateClotheAvailability(clothe);
        conversationUtils.createAndSaveConversation(buyer, clothe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationTemplate> getConversationsBuying(String email) {
        return conversationRepository.findByBuyerEmail(email)
                .map(conversationUtils::mapToTemplate)
                .toList();
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    @Transactional(readOnly = true)
    public List<ConversationTemplate> getConversationsSelling(long clotheId, String email) {
        return conversationRepository.findByClotheId(clotheId)
                .map(conversationUtils::mapToTemplate)
                .toList();
    }

    @Override
    public ConversationDto getConversation(long conversationId, String email) {
        var conversation = conversationUtils.getConversation(conversationId);

        if (!isAuthorizedToSeeMessages(email, conversation.getId(), conversation.getClothe().getId()))
            throw new AccessDeniedException("You don't have permission to see this message");

        return conversationUtils.mapToDto(conversation);
    }

    private void validateOwnership(long clotheId, String email) {
        if (clotheUtils.isOwner(clotheId, email))
            throw new AccessDeniedException("We don't talk to ourselves");
    }

    private boolean isAuthorizedToSeeMessages(String email, long conversationId, long clotheId) {
        return messageutils.isBuyer(conversationId, email) ||
                clotheUtils.isOwner(clotheId, email);
    }
}