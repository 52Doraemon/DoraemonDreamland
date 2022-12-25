package com.example.login1.dao;


import com.example.login1.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDao {

    @Select("select * from user;")
    List<User> SelectAll();

    @Select("select * from user where name = #{name} and password = #{password}")
    User SelectById(@Param("name") String name, @Param("password") String password);

}
