package com.barnackles.user;


public interface UserService  {

    User findUserByEmail(String email);

    User findUserByUserName(String userName);

    void saveUser(User user);

}
