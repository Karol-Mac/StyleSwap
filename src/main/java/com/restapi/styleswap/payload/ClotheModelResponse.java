package com.restapi.styleswap.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

@Data
@NoArgsConstructor
public class ClotheModelResponse {
    private CollectionModel<EntityModel<ClotheDto>> clothes;

    @JsonProperty("page number")
    private int pageNo;

    @JsonProperty("total pages")
    private int totalPages;

    @JsonProperty("page size")
    private int pageSize;

    @JsonProperty("is last")
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
