package com.restapi.styleswap.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationTemplate {
    private long id;

    private long buyerId;

    private long clotheId;
}
