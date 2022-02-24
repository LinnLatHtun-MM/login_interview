package com.llt.login.repository;

import com.llt.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRepository extends JpaRepository<User, Long> {

    List<User> findByEmail(String email);

}
