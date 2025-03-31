package com.restapi.styleswap.utils.assemblers;

import com.restapi.styleswap.controller.ClotheController;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.utils.Constant;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.stream.StreamSupport;

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
                .withRel("all_clothes");
        //TODO: clothe should contain links to: storage, order, conversation and images


        return EntityModel.of(entity, selfLink, allLinks);
    }

    @Override
    public CollectionModel<EntityModel<ClotheDto>> toCollectionModel(Iterable<? extends ClotheDto> entities) {

        List<EntityModel<ClotheDto>> models = StreamSupport
                                                .stream(entities.spliterator(), false)
                                                .map(this::toModel)
                                                .toList();
        Link selfLink;

        selfLink = getSelfLink(models);

        return CollectionModel.of(models, selfLink);
    }

    private Link getSelfLink(List<EntityModel<ClotheDto>> models) {
        Link selfLink;

        if(allUsersSame(models)){
            selfLink = linkTo(methodOn(ClotheController.class)
                    .getAllUserClothes(0, 5, Constant.SORT_BY, Constant.DIRECTION, null))
                    .withSelfRel()
                    .andAffordance(afford(methodOn(ClotheController.class)
                            .createClothe(null, null)));
        } else {
            long categoryId = models.get(0).getContent().getCategoryId();

            selfLink = linkTo(methodOn(ClotheController.class)
                    .getAllClothesFromCategory(categoryId, 0, 5, Constant.SORT_BY, Constant.DIRECTION))
                    .withSelfRel();
        }
        return selfLink;
    }

    private boolean allUsersSame(List<EntityModel<ClotheDto>> models) {
        return models.stream()
                .map(model -> model.getContent().getUserId())
                .distinct()
                .count() == 1;
    }
}