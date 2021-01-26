package com.chijey.startup.security.service.impl;

import com.chijey.startup.security.domain.Message;
import com.chijey.startup.security.repository.MessageRepository;
import com.chijey.startup.security.service.MessageService;
import com.chijey.startup.vo.UserChatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional(rollbackOn = Exception.class)
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> findByToUserIdAndSenderId(String toOpenId, String openId) {
        List<Message> messages = messageRepository.findByToUserIdAndSenderId(toOpenId,openId);
        return messages;
    }

    @Override
    public List<Message> findChatWith(String openId) {
        List<Message> messages = messageRepository.findChatWith(openId);
        return messages;
    }
}
