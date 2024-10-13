package com.restapi.vinted.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {
    private long buyierId;

    private long clotheId;

    @NotNull
    @Length(min = 1, max = 500)
    private String messageContent;

    private boolean isBuyer;
}