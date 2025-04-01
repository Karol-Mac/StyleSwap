package com.restapi.styleswap.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ClotheResponse extends CollectionModel<ClotheDto> {
    private List<ClotheDto> clothes;

    private int pageNo;

    private int totalPages;

    private int pageSize;

    private boolean isLast;
}