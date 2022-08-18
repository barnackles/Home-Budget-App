package com.barnackles.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CurrentUser extends User {

    private final com.barnackles.user.User user;
    private final Long id;

    public CurrentUser(String userName, String password, Collection<? extends GrantedAuthority> authorities,
                       com.barnackles.user.User user, Long id) {
        super(userName, password, authorities);
        this.id = id;
        this.user = user;
    }

    public com.barnackles.user.User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }
}
