package com.supportdesk.security.utils;

import com.supportdesk.security.custom.CustomUserDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@UtilityClass
public class SecurityUtils {

    public CustomUserDetails getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return (CustomUserDetails) authentication.getPrincipal();
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getUser().getId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getUsername();
    }

}