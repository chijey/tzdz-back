package com.chijey.startup.security.service;

import com.chijey.startup.security.domain.Message;

import java.util.List;


public interface MessageService {
    Message save(Message message);

    List<Message> findByToUserIdAndSenderId(String toOpenId, String openId);
}
