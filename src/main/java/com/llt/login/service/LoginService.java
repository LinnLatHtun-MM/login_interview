package com.llt.login.service;

import com.llt.login.model.User;
import org.springframework.http.ResponseEntity;

public interface LoginService {

    ResponseEntity login(String email, String password);

    ResponseEntity register(User user);

    ResponseEntity update(User user);

}
