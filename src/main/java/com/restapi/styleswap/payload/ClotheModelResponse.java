package com.restapi.styleswap.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ClotheModelResponse extends RepresentationModel<ClotheModelResponse> {
    private CollectionModel<EntityModel<ClotheDto>> clothes;

    @JsonProperty("page_number")
    private int pageNo;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("is_last")
    private boolean isLast;

    public static ClotheModelResponse from(ClotheResponse clotheResponse) {
        ClotheModelResponse response = new ClotheModelResponse();
        response.setPageNo(clotheResponse.getPageNo());
        response.setTotalPages(clotheResponse.getTotalPages());
        response.setPageSize(clotheResponse.getPageSize());
        response.setLast(clotheResponse.isLast());

        return response;
    }
}
