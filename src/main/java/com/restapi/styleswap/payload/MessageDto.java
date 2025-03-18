package com.restapi.styleswap.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {

    @NotNull
    @Length(min = 1, max = 250)
    private String messageContent;

    private Boolean ifFromBuyer;

    private ZonedDateTime createdAt;
}
