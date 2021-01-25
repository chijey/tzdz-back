package com.chijey.startup.security.repository;

import com.chijey.startup.security.domain.Message;
import com.chijey.startup.security.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, String> , JpaSpecificationExecutor<Message> {

}
