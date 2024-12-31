package com.restapi.styleswap.repository;

import com.restapi.styleswap.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
