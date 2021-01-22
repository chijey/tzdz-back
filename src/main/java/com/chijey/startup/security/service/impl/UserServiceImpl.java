package com.chijey.startup.security.service.impl;

import com.chijey.startup.param.PersonDTO;
import com.chijey.startup.security.domain.UserInfo;
import com.chijey.startup.security.repository.UserRepository;
import com.chijey.startup.common.base.Result;
import com.chijey.startup.security.domain.User;
import com.chijey.startup.security.service.UserService;
import com.chijey.startup.utils.RepositoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户接口实现类
 *
 * @author zhuhuix
 * @date 2020-04-03
 */
@Slf4j
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<User> create(User user) {
        return new Result<User>().ok(userRepository.save(user));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<User> update(User user) {
        return new Result<User>().ok(userRepository.save(user));
    }

    @Override
    public Result<User> update(User user, String openId) {
        User target = userRepository.findByOpenId(openId);
        if (target != null) {
            BeanUtils.copyProperties(user, target, RepositoryUtil.getNullPropertyNames(user));
        } else {
            target = user;
        }
        return new Result<User>().ok(userRepository.save(target));
    }

    @Override
    public Result<User> findById(Long id) {
        Optional<User> optional = userRepository.findById(id);
        return optional.map(user -> new Result<User>().ok(user)).orElse(null);
    }

    @Override
    public User findByOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    @Override
    public Result<User> findByUserName(String userName) {
        return new Result<User>().ok(userRepository.findByUserName(userName));
    }

}
