package com.restapi.styleswap.utils.assemblers;

import com.restapi.styleswap.controller.ClotheController;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.utils.Constant;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ClotheModelAssembler implements RepresentationModelAssembler<ClotheDto, EntityModel<ClotheDto>> {

    @Override
    public EntityModel<ClotheDto> toModel(ClotheDto entity) {
        final var selfLink = linkTo(methodOn(ClotheController.class)
                                .getClothe(entity.getId(), null))
                                .withSelfRel()
                                .andAffordance(afford(methodOn(ClotheController.class)
                                        .updateClothe(entity.getId(), entity, null)))
                                .andAffordance(afford(methodOn(ClotheController.class)
                                        .deleteClothe(entity.getId(), null)));

        final var allLinks = linkTo(methodOn(ClotheController.class)
                .getAllClothesFromCategory(entity.getCategoryId(), 0, 5, Constant.SORT_BY, Constant.DIRECTION))
                .withRel("allClothes");

        return EntityModel.of(entity, selfLink, allLinks);
    }

    @Override
    public CollectionModel<EntityModel<ClotheDto>> toCollectionModel(Iterable<? extends ClotheDto> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
