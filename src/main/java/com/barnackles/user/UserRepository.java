package com.barnackles.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUserName(String userName);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long id);

    void deleteUserById(Long id);

    List<User> findAll();

    @Override
    Page<User> findAll(Pageable pageable);

}
