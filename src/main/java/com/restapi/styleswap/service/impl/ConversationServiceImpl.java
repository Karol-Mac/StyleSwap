package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.payload.ConversationTemplate;
import com.restapi.styleswap.repository.ConversationRepository;
import com.restapi.styleswap.service.ConversationService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.ConversationUtils;
import com.restapi.styleswap.utils.UserUtils;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ClotheUtils clotheUtils;
    private final UserUtils userUtils;
    private final ConversationUtils conversationUtils;

    public ConversationServiceImpl(ConversationRepository conversationRepository, ClotheUtils clotheUtils,
                                   UserUtils userUtils, ConversationUtils conversationUtils) {
        this.conversationRepository = conversationRepository;
        this.clotheUtils = clotheUtils;
        this.userUtils = userUtils;
        this.conversationUtils = conversationUtils;
    }

    @Override
    @PreAuthorize("!@clotheUtils.isOwner(#clotheId, #email)" +
                    "and @clotheUtils.getClotheFromDB(#clotheId).isAvailable()")
    @Transactional
    public void startConversation(long clotheId, String email) {
        var buyer = userUtils.getUser(email);

        conversationUtils.createAndSaveConversation(buyer, clotheId);
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
    @PostAuthorize("returnObject.buyerId == @userUtils.getUser(#email).getId()" +
                    "or @clotheUtils.isOwner(returnObject.clotheId, #email)")
    public ConversationDto getConversation(long conversationId, String email) {
        var conversation = conversationUtils.getConversation(conversationId);

        return conversationUtils.mapToDto(conversation);
    }
}