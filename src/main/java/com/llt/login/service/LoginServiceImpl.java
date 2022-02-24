package com.llt.login.service;

import com.llt.login.Util.Constant;
import com.llt.login.ErrorMessage.ErrorMessage;
import com.llt.login.Util.MD5Utils;
import com.llt.login.model.Response;
import com.llt.login.model.User;
import com.llt.login.repository.LoginRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    LoginRepository repo;

    private static final Logger logger = LogManager.getLogger(LoginService.class);

    @Override
    public ResponseEntity login(String email,String password) {
        if(email==null || email.trim().equals(""))
        {
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.Email_Value_Missing,null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if(!isEmail(email.trim()))
        {
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.Invalid_Email, null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if(password==null ||password.trim().equals(""))
        {
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.Password_Value_Missing, null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        List<User> userList=new ArrayList<>();
        userList = repo.findByEmail(email);
        if(userList.isEmpty() || userList==null){
            logger.warn("Can't find by email: {}", email);
            Response response = getResponseObj( Constant.FAIL,"Email is Wrong!", null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if(userList.size()>1){
            logger.warn("Database response size is more than one, duplicate size: {}, data: {}", userList.size(), userList);
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.System_Error, null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        MD5Utils md5Utils=new MD5Utils();
        String hashPwd=md5Utils.digest(password);
        if(!hashPwd.equals(userList.get(0).getPassword()))
        {
            logger.warn("Password is Incorrect: {}, Correct Password: {}", hashPwd, userList.get(0).getPassword());
            Response response = getResponseObj( Constant.FAIL,"Incorrect Password!!", null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        else
        {
            logger.warn("Password is Correct: {}", hashPwd);
            Response response = getResponseObj( Constant.SUCCESS,"", null);
            return new ResponseEntity(response, HttpStatus.OK);
        }

    }

    @Override
    public ResponseEntity register(User user) {
        if(user == null) {
            logger.warn("input user is null");
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.RequestObject_Missing,null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if(user.getName()==null || user.getName().trim().equals(""))
        {
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.Name_Value_Missing,null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if(user.getEmail()==null || user.getEmail().trim().equals(""))
        {
        Response response = getResponseObj( Constant.FAIL,ErrorMessage.Email_Value_Missing, null);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if(!isEmail(user.getEmail().trim()))
        {
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.Invalid_Email, null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        if(user.getPassword()==null || user.getPassword().trim().equals(""))
        {
            Response response = getResponseObj(ErrorMessage.Password_Value_Missing, Constant.FAIL,null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        List<User> userList = repo.findByEmail(user.getEmail());

        if(!userList.isEmpty()){
            logger.warn("User is already existed!!!");
            Response response =getResponseObj("User is already existed!!!",Constant.FAIL, new ArrayList<>());
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        String password=user.getPassword().trim();
        MD5Utils md5Utils=new MD5Utils();
        String hashPwd=md5Utils.digest(password);

        user.setPassword(hashPwd);  // overwrite hash password instead of plain text password.

        logger.info("Before saving User: {}", user);
        repo.save(user);

        logger.info("Successfully Inserted Data");

        Response response =getResponseObj("",Constant.SUCCESS,"" );

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity update(User user) {
        if(user == null) {
            logger.warn("input user is null");
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.RequestObject_Missing,null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        if(!isEmail(user.getEmail().trim()))
        {
            Response response = getResponseObj( Constant.FAIL,ErrorMessage.Invalid_Email, null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        Optional<User> userDB=repo.findById(user.getId());
        if(userDB.isPresent()) {
            User userResult = userDB.get();

            logger.info("Database Data: {}", userResult);
            logger.info("User Input Data: {}", user);

            boolean isUpdate=false;
            if(user.getName()!=null && !user.getName().trim().equals("") && !user.getName().equals(userResult.getName()))
            {
                userResult.setName(user.getName());
                isUpdate=true;
            }

            if(user.getEmail()!=null && !user.getEmail().trim().equals("") && !user.getEmail().equals(userResult.getEmail()))
            {
                userResult.setEmail(user.getEmail());
                isUpdate=true;
            }

            MD5Utils md5Utils=new MD5Utils();
            String hash=md5Utils.digest(user.getPassword());
            user.setPassword(hash);
            if(user.getPassword()!=null && !user.getPassword().trim().equals("") && !hash.equals(userResult.getPassword()))
            {
                userResult.setPassword(hash);
                isUpdate=true;
            }

            if(!isUpdate)
            {
                logger.warn("Same Data Update Error");
                Response response = getResponseObj( Constant.FAIL,ErrorMessage.Same_Data_Update_Error,null);
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
            repo.save(userResult);
            Response response = getResponseObj( Constant.SUCCESS,"Update Successfully",userResult);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        else
        {
            logger.warn("We can't find any data with input id: {} "+user.getId());
            Response response = getResponseObj( Constant.FAIL,"We can't find any data with input id: "+user.getId(),null);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

    }

    public static boolean isEmail(String email) {
        String regex = "^[\\w!#$%&'+/=?`{|}~^-]+(?:\\.[\\w!#$%&'+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);
        boolean b = matcher.matches();
        System.out.println("Email Validation: " + b);
        return b;
    }

    private Response getResponseObj(String message, String statusStr, Object data){
        Response response = new Response();
        response.setMessage(message);
        response.setStatus(statusStr);
        response.setData(data);
        return response;
    }
}
