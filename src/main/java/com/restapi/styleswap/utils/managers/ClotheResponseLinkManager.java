package com.restapi.styleswap.utils.managers;

import com.restapi.styleswap.controller.ClotheController;
import com.restapi.styleswap.payload.ClotheResponse;
import com.restapi.styleswap.utils.Constant;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClotheResponseLinkManager {

    public void addPaginationLinks(ClotheResponse clotheResponse) {
        addPrevPageLink(clotheResponse);
        addNextAndLastLink(clotheResponse);
    }

    private void addNextAndLastLink(ClotheResponse clotheResponse) {
        if (!clotheResponse.isLast()) {
            clotheResponse.add(linkTo(methodOn(ClotheController.class)
                    .getAllClothesFromCategory(
                            clotheResponse.getClothes().get(0).getCategoryId(),
                            clotheResponse.getPageNo() + 1,
                            clotheResponse.getPageSize(),
                            Constant.SORT_BY, Constant.DIRECTION))
                    .withRel("next"));

            clotheResponse.add(linkTo(methodOn(ClotheController.class)
                    .getAllClothesFromCategory(
                            clotheResponse.getClothes().get(0).getCategoryId(),
                            clotheResponse.getTotalPages() - 1,
                            clotheResponse.getPageSize(),
                            Constant.SORT_BY, Constant.DIRECTION))
                    .withRel("last"));
        }
    }

    private void addPrevPageLink(ClotheResponse clotheResponse) {
        if (clotheResponse.getPageNo() > 0) {
            clotheResponse.add(linkTo(methodOn(ClotheController.class)
                    .getAllClothesFromCategory(
                            clotheResponse.getClothes().get(0).getCategoryId(),
                            clotheResponse.getPageNo() - 1,
                            clotheResponse.getPageSize(),
                            Constant.SORT_BY, Constant.DIRECTION))
                    .withRel("prev")
            );
        }
    }
}
