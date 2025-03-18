package com.restapi.styleswap.service;

public interface MessageService {

    void sendMessage(long conversationId, String message, String email);
}
