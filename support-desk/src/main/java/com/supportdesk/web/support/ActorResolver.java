package com.supportdesk.web.support;

import com.supportdesk.domain.enums.Role;
import com.supportdesk.domain.model.Actor;
import com.supportdesk.infrastructure.security.CustomUserDetails;
import org.springframework.stereotype.Component;

@Component
public class ActorResolver {
    public Actor resolve(CustomUserDetails principal) {
        return new Actor(principal.getUserId(), Role.valueOf(principal.getRole()));
    }
}