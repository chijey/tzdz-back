package com.chijey.startup.security.repository;

import com.chijey.startup.security.domain.Message;
import com.chijey.startup.security.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> , JpaSpecificationExecutor<Message> {

    @Modifying(clearAutomatically = true)
    @Query(value = " select m from Message m where  m.toUserId in(?1,?2) and m.senderOpenId in(?1,?2) order by m.createTime asc")
    List<Message>  findByToUserIdAndSenderId(String toUserId, String senderId);

    @Modifying(clearAutomatically = true)
    @Query(value = " select m from Message m where  m.toUserId=?1 or m.senderOpenId=?1 group by m.chatId order by m.createTime asc")
    List<Message> findChatWith(String openId);
}
