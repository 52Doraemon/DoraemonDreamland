package com.example.login1.controller;

import com.example.login1.dao.UserDao;
import com.example.login1.domain.Result;
import com.example.login1.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Controller
public class UserController {

    @Autowired
    UserDao userDao;

    @RequestMapping("/login")
    public ModelAndView loginPage(){
        return new ModelAndView("login.html");
    }

    @CrossOrigin
    @PostMapping("/login")
    Result getById(@RequestParam("name") String name, @RequestParam("password") String password) {
        Result result;
        System.out.println(name);
        System.out.println(password);
        User user = userDao.SelectById(name, password);
        if (user == null) {
            result = new Result(false, null);
        } else {
            result = new Result(true, user);
        }
        return result;
    }

    @PostMapping("/test")
    String get() {
        return "hello";
    }

}
