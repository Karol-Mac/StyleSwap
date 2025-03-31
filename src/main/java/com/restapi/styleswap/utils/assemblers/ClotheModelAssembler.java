package com.restapi.styleswap.utils.assemblers;

import com.restapi.styleswap.controller.*;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.utils.Constant;
import com.stripe.exception.StripeException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ClotheModelAssembler implements RepresentationModelAssembler<ClotheDto, EntityModel<ClotheDto>> {

    @Override
    public EntityModel<ClotheDto> toModel(ClotheDto entity) {

        final var selfLinks = getClotheLink(entity);
        final var conversationLinks = getConversationLinks(entity);
        final var storageLinks = getStorageLinks(entity);
        final var imageLinks = getImageLinks(entity);
        final var orderLink = getOrderLink(entity);

        List<Link> links = new ArrayList<>();
        links.addAll(selfLinks);
        links.addAll(conversationLinks);
        links.addAll(storageLinks);
        links.addAll(imageLinks);

        links.add(orderLink);

        return EntityModel.of(entity, links);
    }

    @Override
    public CollectionModel<EntityModel<ClotheDto>> toCollectionModel(Iterable<? extends ClotheDto> entities) {

        List<EntityModel<ClotheDto>> models = StreamSupport
                                                .stream(entities.spliterator(), false)
                                                .map(this::toModel)
                                                .toList();

        Link selfLink = getSelfLink(models);
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

    private Link getOrderLink(ClotheDto entity) {
        try {
            return linkTo(methodOn(OrderController.class)
                    .order(entity.getId(), null))
                    .withRel("order_clothe")
                    .withType("POST");
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Link> getStorageLinks(ClotheDto entity) {
        Link addToStorage = linkTo(methodOn(StorageController.class)
                .addClothe(entity.getId(), null))
                .withRel("add_to_storage")
                .withType("POST");

        Link removeFromStorage = linkTo(methodOn(StorageController.class)
                .removeClothe(entity.getId(), null))
                .withRel("remove_from_storage")
                .withType("DELETE");

        return List.of(addToStorage, removeFromStorage);
    }

    private List<Link> getConversationLinks(ClotheDto entity) {
        Link startConversation = linkTo(methodOn(ConversationController.class)
                .startConversation(entity.getId(), null))
                .withRel("start_conversation")
                .withType("POST");

        Link conversationSelling = linkTo(methodOn(ConversationController.class)
                .getConversationsSelling(entity.getId(), null))
                .withRel("conversation_selling");

        return List.of(startConversation, conversationSelling);
    }

    private List<Link> getImageLinks(ClotheDto entity) {
        Link updateImage = linkTo(methodOn(ImageController.class)
                .uploadImage(entity.getId(), null, null))
                .withRel("update_images")
                .withType("POST");

        Link deleteImage = linkTo(methodOn(ImageController.class)
                .deleteImage(entity.getId(), null, null))
                .withRel("delete_images")
                .withType("DELETE");

        return List.of(updateImage, deleteImage);
    }

    private List<Link> getClotheLink(ClotheDto entity) {
        final var selfLink = linkTo(methodOn(ClotheController.class)
                .getClothe(entity.getId(), null))
                .withSelfRel()
                .andAffordance(afford(methodOn(ClotheController.class)
                        .updateClothe(entity.getId(), entity, null)))
                .andAffordance(afford(methodOn(ClotheController.class)
                        .deleteClothe(entity.getId(), null)));

        final Link allLinks = linkTo(methodOn(ClotheController.class)
                .getAllClothesFromCategory(entity.getCategoryId(), 0, 5, Constant.SORT_BY, Constant.DIRECTION))
                .withRel("all_clothes");
        return List.of(selfLink, allLinks);
    }
}