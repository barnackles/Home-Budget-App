package com.barnackles.user;


public interface UserService  {

    User findUserByEmail(String email);

    User findUserByUserName(String userName);

    User saveUser(User user);

}
