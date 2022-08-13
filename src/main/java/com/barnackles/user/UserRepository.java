package com.barnackles.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUserName(String userName);
    User findUserByEmail(String email);
    User findUserById(Long id);
    void deleteUserById(Long id);






}
