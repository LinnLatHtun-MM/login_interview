package com.llt.login.controller;

import com.llt.login.ErrorMessage.ErrorMessage;
import com.llt.login.model.User;
import com.llt.login.service.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loginApi")
public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);
    ResponseEntity responseEntity;
    @Autowired
    LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity signUp(@RequestBody User user) {
        logger.info("==================== Start SingUp method!!! ====================");


        try {
            responseEntity = loginService.register(user);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            responseEntity = new ResponseEntity(ErrorMessage.System_Error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Response : {} ", responseEntity);
        logger.info("==================== End SingUp method!!! ====================");
        return responseEntity;
    }

    @PostMapping("/login")
    public ResponseEntity Login(@RequestParam String email, @RequestParam String password) {
        logger.info("==================== Start Login method!!! ====================");

        ResponseEntity responseEntity;
        try {
            responseEntity = loginService.login(email, password);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            responseEntity = new ResponseEntity(ErrorMessage.System_Error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Response : {} ", responseEntity);
        logger.info("==================== End Login method!!! ====================");
        return responseEntity;
    }

    @PostMapping("/update")
    public ResponseEntity Update(@RequestBody User user) {
        logger.info("==================== Start Update method!!! ====================");

        ResponseEntity responseEntity;
        try {
            responseEntity = loginService.update(user);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            responseEntity = new ResponseEntity(ErrorMessage.System_Error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Response : {} ", responseEntity);
        logger.info("==================== End Update method!!! ====================");
        return responseEntity;
    }
}
