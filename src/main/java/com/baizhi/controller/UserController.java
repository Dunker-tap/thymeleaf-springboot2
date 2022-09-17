package com.baizhi.controller;

import com.baizhi.entity.User;
//import com.baizhi.service.UserService;
import com.baizhi.service.UserServiceImpl;
import com.baizhi.utils.VerifyCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 用户登录方法
     */

    @RequestMapping("login")
    public String login(String username,String password,HttpSession session){
        log.debug("本次登录用户名: {}",username);
        log.debug("本次登录密码: {}",password);
        //调用业务层进行登录
        try {
            User user = userService.login(username,password);
            //保存用户信息到session对象中
            session.setAttribute("user",user);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";
        }
        //登录成功之后应该先跳转到查询所有员工信息控制器路径，
        //然后再由查询所有员工信息控制器跳转到对应的前端页面
        return "redirect:/employee/lists";
    }

    /**
     * 用户注册
     * @return
     */
    @RequestMapping("register")
    public String register(User user, String code,HttpSession session){
        log.debug("用户名: {},真是姓名: {},密码: {},性别: {},",user.getUsername(),user.getRealname(),user.getPassword(),user.getGender());
        log.debug("用户输入验证码: {}",code);

        try {
            //1.判断用户输入验证码和session中验证码是否一致
            String sessionCode = session.getAttribute("code").toString();
            if(!sessionCode.equalsIgnoreCase(code))throw new RuntimeException("验证码输入错误!");
            //如果比较通过了，进行用户注册
            //2.注册用户
            userService.register(user);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "redirect:/register"; //注册失败重新回到注册
        }
        return  "redirect:/login";  //注册成功跳转到登录
    }
    /**
     * 1.生成4位随机数
     * 2.保存到session作用域
     * 3.根据随机数生成图片
     * 4.通过response响应图片
     */
    @RequestMapping("generateImageCode")
    public void generateImageCode(HttpSession session, HttpServletResponse response) throws IOException {
        //1.生成4位随机数
        String code = VerifyCodeUtils.generateVerifyCode(4);
        //2.保存到session作用域
        session.setAttribute("code",code);
        //3.根据随机数生成图片 && 4.通过response响应图片  && 5.设置响应类型
        response.setContentType("image/png");
        ServletOutputStream os = response.getOutputStream();
        VerifyCodeUtils.outputImage(220,60, os,code);
    }
}
