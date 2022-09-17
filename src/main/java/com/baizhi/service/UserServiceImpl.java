package com.baizhi.service;

import com.baizhi.dao.UserDao;
import com.baizhi.entity.User;
import com.baizhi.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;

@Controller
//控制事务
@Transactional
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void register(User user) {
        //1.根据用户名查询数据库中是否存在改用户
        User userDB = userDao.findByUserName(user.getUsername());
        //2.判断用户是否存在
        if(!ObjectUtils.isEmpty(userDB)) throw new RuntimeException("当前用户名已被注册!");
        //3.注册用户&明文的密码加密  特点: 相同字符串多次使用md5就行加密 加密结果始终是一致
        String newPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(newPassword);
        userDao.save(user);
    }

    @Override
    public User login(String username, String password) {
        //先通过用户名去数据库中找对应的用户信息
        User user = userDao.findByUserName(username);
        if(ObjectUtils.isEmpty(user)) throw new RuntimeException("用户名不正确");
        String passwordSecret = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if(!user.getPassword().equals(passwordSecret)) throw new RuntimeException("密码输入错误");
        return user;
    }

}
