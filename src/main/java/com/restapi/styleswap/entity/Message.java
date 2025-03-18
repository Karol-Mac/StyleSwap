package com.restapi.styleswap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Embeddable
public class Message {
    @Column(nullable = false, length = 250)
    private String content;

    private Boolean ifFromBuyer;

    @CreationTimestamp
    private ZonedDateTime createdAt;
}