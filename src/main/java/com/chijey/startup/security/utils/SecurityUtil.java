package com.chijey.startup.security.utils;

import com.chijey.startup.security.config.JwtSecurityProperties;
import com.chijey.startup.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;

@Slf4j
public class SecurityUtil {
    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    public static String getCurrentUserOpenId() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication.getPrincipal() instanceof User) {
            User securityUser = (User) authentication.getPrincipal();
            return securityUser.getUsername();
        }
        return null;
    }
}
