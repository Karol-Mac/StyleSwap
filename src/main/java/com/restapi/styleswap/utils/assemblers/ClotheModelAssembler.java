package com.restapi.styleswap.utils.assemblers;

import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.utils.managers.ClotheDtoLinkManager;
import com.restapi.styleswap.utils.managers.ClotheResponseLinkManager;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ClotheModelAssembler implements RepresentationModelAssembler<ClotheDto, ClotheDto> {

    private final ClotheDtoLinkManager clotheDtoLinkManager;
    private final ClotheResponseLinkManager clotheResponseLinkManager;

    public ClotheModelAssembler(ClotheDtoLinkManager clotheDtoLinkManager, ClotheResponseLinkManager clotheResponseLinkManager) {
        this.clotheDtoLinkManager = clotheDtoLinkManager;
        this.clotheResponseLinkManager = clotheResponseLinkManager;
    }


    @Override
    public ClotheDto toModel(ClotheDto entity) {

        clotheDtoLinkManager.addEntityLinks(entity);
        return entity;
    }

    public ClotheResponse toCollectionModel(ClotheResponse entity) {

        var clotheModels = entity.getClothes().stream()
                .map(this::toModel)
                .toList();

        entity.setClothes(clotheModels);
        clotheResponseLinkManager.addPaginationLinks(entity);

        return entity;
    }
}