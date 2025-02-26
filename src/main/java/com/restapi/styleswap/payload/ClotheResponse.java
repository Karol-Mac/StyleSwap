package com.restapi.styleswap.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ClotheResponse {
    private List<ClotheDto> clothes;

    private int pageNo;

    private int totalPages;

    private int pageSize;

    private boolean isLast;
}