package com.restapi.styleswap.service.impl;

import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.entity.User;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.payload.ConversationDto;
import com.restapi.styleswap.repository.ConversationRepository;
import com.restapi.styleswap.service.ConversationService;
import com.restapi.styleswap.utils.ClotheUtils;
import com.restapi.styleswap.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ClotheUtils clotheUtils;
    private final UserUtils userUtils;

    public ConversationServiceImpl(ConversationRepository conversationRepository, ClotheUtils clotheUtils, UserUtils userUtils) {
        this.conversationRepository = conversationRepository;
        this.clotheUtils = clotheUtils;
        this.userUtils = userUtils;
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
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    @Transactional(readOnly = true)
    public List<ConversationDto> getConversationsSelling(long clotheId, String email) {
        return conversationRepository.findByClotheId(clotheId)
                .map(this::mapToDto)
                .toList();
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

    public ConversationDto mapToDto(Conversation conversation) {
        return ConversationDto.builder()
                .buyerId(conversation.getBuyer().getId())
                .clotheId(conversation.getClothe().getId())
                .id(conversation.getId())
                .build();
    }
}
