package com.restapi.styleswap.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private long id;

    private String name;

    private String description;

    private long clothesCount;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;
}
