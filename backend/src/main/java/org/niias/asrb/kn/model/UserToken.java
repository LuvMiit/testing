package org.niias.asrb.kn.model;

import org.niias.asrb.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;

public class UserToken extends UsernamePasswordAuthenticationToken {
    private User user;

    public UserToken(User user) {
        super(user, null, Arrays.asList(new SimpleGrantedAuthority("USER")));
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
