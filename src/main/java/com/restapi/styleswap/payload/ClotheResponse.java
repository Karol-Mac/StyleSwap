package com.restapi.styleswap.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ClotheResponse {
    private List<ClotheDto> clothes;
    @JsonProperty("page number")

    private int pageNo;

    @JsonProperty("total pages")
    private int totalPages;

    @JsonProperty("page size")
    private int pageSize;

    @JsonProperty("is last")
    private boolean isLast;
}
