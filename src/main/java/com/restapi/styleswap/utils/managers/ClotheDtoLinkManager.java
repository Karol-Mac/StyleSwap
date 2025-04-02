package com.restapi.styleswap.utils.managers;

import com.restapi.styleswap.controller.*;
import com.restapi.styleswap.payload.ClotheDto;
import com.restapi.styleswap.utils.Constant;
import com.stripe.exception.StripeException;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClotheDtoLinkManager {

    public void addEntityLinks(ClotheDto entity) {
        addClotheLinks(entity);
        addImageLinks(entity);
        addConversationLinks(entity);
        addStorageLinks(entity);
        addOrderLink(entity);
    }

    private void addOrderLink(ClotheDto entity) {
        try {
            entity.add(linkTo(methodOn(OrderController.class)
                    .order(entity.getId(), null))
                    .withRel("order_clothe")
                    .withType("POST"));
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private void addStorageLinks(ClotheDto entity) {
        Link addToStorage = linkTo(methodOn(StorageController.class)
                .addClothe(entity.getId(), null))
                .withRel("add_to_storage")
                .withType("POST");

        Link removeFromStorage = linkTo(methodOn(StorageController.class)
                .removeClothe(entity.getId(), null))
                .withRel("remove_from_storage")
                .withType("DELETE");

        entity.add(addToStorage, removeFromStorage);
    }

    private void addConversationLinks(ClotheDto entity) {
        Link startConversation = linkTo(methodOn(ConversationController.class)
                .startConversation(entity.getId(), null))
                .withRel("start_conversation")
                .withType("POST");

        Link conversationSelling = linkTo(methodOn(ConversationController.class)
                .getConversationsSelling(entity.getId(), null))
                .withRel("conversation_selling");

        entity.add(startConversation, conversationSelling);
    }

    private void addImageLinks(ClotheDto entity) {
        Link updateImage = linkTo(methodOn(ImageController.class)
                .uploadImage(entity.getId(), null, null))
                .withRel("update_images")
                .withType("POST");

        Link deleteImage = linkTo(methodOn(ImageController.class)
                .deleteImage(entity.getId(), null, null))
                .withRel("delete_images")
                .withType("DELETE");

        entity.add(updateImage, deleteImage);
    }

    private void addClotheLinks(ClotheDto entity) {
        Link selfLink = linkTo(methodOn(ClotheController.class)
                .getClothe(entity.getId(), null))
                .withSelfRel()
                .andAffordance(afford(methodOn(ClotheController.class)
                        .updateClothe(entity.getId(), entity, null)))
                .andAffordance(afford(methodOn(ClotheController.class)
                        .deleteClothe(entity.getId(), null)));

        Link collectionLink = linkTo(methodOn(ClotheController.class)
                .getAllClothesFromCategory(entity.getCategoryId(), 0, 5, Constant.SORT_BY, Constant.DIRECTION))
                .withRel("all_clothes");

        entity.add(selfLink, collectionLink);
    }
}
