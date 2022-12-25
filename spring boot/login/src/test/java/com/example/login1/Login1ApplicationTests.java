package com.example.login1;

import com.example.login1.dao.UserDao;
import com.example.login1.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Login1ApplicationTests {

    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
        //User u=userDao.SelectById(1);
        //System.out.println(u.getName());
    }

}
