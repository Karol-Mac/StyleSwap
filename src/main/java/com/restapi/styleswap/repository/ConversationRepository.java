package com.restapi.styleswap.repository;

import com.restapi.styleswap.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Stream<Conversation> findByClotheId(Long clotheId);

    Stream<Conversation> findByBuyerEmail(String email);

    boolean existsByIdAndBuyerEmail(Long id, String email);
}
