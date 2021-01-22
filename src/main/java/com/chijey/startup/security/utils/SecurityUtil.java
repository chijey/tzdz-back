package com.chijey.startup.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;


public class SecurityUtil {

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
