package com.restapi.styleswap.service;

import com.restapi.styleswap.payload.MessageDto;

import java.util.List;

public interface MessageService {

    List<MessageDto> getMessages(long conversationId, String email);

    void sendMessage(long conversationId, String message, String email);
}
