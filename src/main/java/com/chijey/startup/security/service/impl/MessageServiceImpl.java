package com.chijey.startup.security.service.impl;

import com.chijey.startup.security.domain.Message;
import com.chijey.startup.security.repository.MessageRepository;
import com.chijey.startup.security.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }
}
