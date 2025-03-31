package com.restapi.styleswap.utils;

import com.restapi.styleswap.entity.Category;
import com.restapi.styleswap.entity.Clothe;
import com.restapi.styleswap.entity.Conversation;
import com.restapi.styleswap.exception.ApiException;
import com.restapi.styleswap.exception.ResourceNotFoundException;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.repository.ClotheRepository;
import com.restapi.styleswap.repository.ConversationRepository;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ClotheUtils {

    private final ClotheRepository clotheRepository;
    private final ConversationRepository conversationRepository;

    public ClotheUtils(ClotheRepository clotheRepository, ConversationRepository conversationRepository) {
        this.clotheRepository = clotheRepository;
        this.conversationRepository = conversationRepository;
    }


    @Transactional(readOnly = true)
    public boolean isOwner(long clotheId, String email) {
        return clotheRepository.existsByIdAndUserEmail(clotheId, email);
    }

    public Clothe getClotheFromDB(long id) {
        return clotheRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Clothe", "id", id)
        );
    }

    public void validateClotheAvailability(Clothe clothe) {
        if (!clothe.isAvailable())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Clothe is not available");
    }

    @Transactional
    public ClotheDto saveClotheInDB(Clothe clothe) {
        return mapToDto(clotheRepository.save(clothe));
    }

    public ClotheResponse getClotheResponse(int pageNo, int pageSize, Page<Clothe> clothes) {
        ClotheResponse clotheResponse = new ClotheResponse();

        clotheResponse.setClothes(clothes.stream().map(this::mapToDto).toList());
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setTotalPages(clothes.getTotalPages());
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(clothes.isLast());
        return clotheResponse;
    }

    public Clothe mapToEntity(ClotheDto clotheDto) {
        return Clothe.builder()
                .name(clotheDto.getName())
                .description(clotheDto.getDescription())
                .price(clotheDto.getPrice())
                .size(clotheDto.getSize())
                .material(clotheDto.getMaterial())
                .images(clotheDto.getImages())
                .isAvailable(clotheDto.getIsAvailable())
                .views(clotheDto.getViews())
                .category(new Category(clotheDto.getCategoryId()))
                .build();
    }

    @Transactional(readOnly = true)
    public ClotheDto mapToDto(Clothe clothe) {
        var conversations = conversationRepository
                                .findByClotheId(clothe.getId())
                                .map(Conversation::getId)
                                .toList();

        return ClotheDto.builder()
                .id(clothe.getId())
                .name(clothe.getName())
                .description(clothe.getDescription())
                .price(clothe.getPrice())
                .size(clothe.getSize())
                .material(clothe.getMaterial())
                .images(clothe.getImages())
                .views(clothe.getViews())
                .isAvailable(clothe.isAvailable())
                .createdAt(clothe.getCreatedAt())
                .updatedAt(clothe.getUpdatedAt())
                .userId(clothe.getUser().getId())
                .categoryId(clothe.getCategory().getId())
                .conversationIds(conversations)
                .build();
    }
}