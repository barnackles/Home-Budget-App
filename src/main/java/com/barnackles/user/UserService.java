package com.barnackles.user;


import java.util.List;

public interface UserService  {

    User findUserByEmail(String email);

    User findUserByUserName(String userName);

    User findUserById(Long id);

    List<User> findAll();

    List<User> findAll(int pageNumber, int pageSize, String sortBy);

    User saveUser(User user);

    User updateUser(User user);

    User updateUserPassword(User user);
    void deleteUser(User user);






}
