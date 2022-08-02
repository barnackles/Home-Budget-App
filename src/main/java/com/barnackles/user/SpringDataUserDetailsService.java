package com.barnackles.user;

import com.barnackles.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SpringDataUserDetailsService implements UserDetailsService {

    private UserService userService;


    @Autowired
    public void setUserRepository(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user;
        if (isLoginUserNameOrEmail(login)) {
            user = userService.findUserByEmail(login);
        }
        else {
            user = userService.findUserByUserName(login);
        }
        if (user == null) {
            throw new UsernameNotFoundException(login);
        }

        List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
        return buildUserForAuthentication(user, authorities);
    }

    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        for (Role role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return new ArrayList<>(roles);
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
//        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
//                user.getActive(), true, true, true, authorities);
        return new CurrentUser(user.getUserName(), user.getPassword(), authorities, user);
    }

    public boolean isLoginUserNameOrEmail(String login) {
        return login.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }




}
