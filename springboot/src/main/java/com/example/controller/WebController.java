package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    private UserService userService;


    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello() {
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
public Result login(@RequestBody Account account) {
    // 在管理员表中查找
    try {
        Account adminAccount = adminService.login(account);
        if (adminAccount != null) {
            adminAccount.setRole("ADMIN");
            return Result.success(adminAccount);
        }
    } catch (CustomException e) {
        // 管理员表中未找到，继续查找用户表
    }
    
    // 在用户表中查找
    try {
        Account userAccount = userService.login(account);
        if (userAccount != null) {
            userAccount.setRole("USER");
            return Result.success(userAccount);
        }
    } catch (CustomException e) {
        return Result.error("账号或密码错误");
    }
    
    return Result.error("账号或密码错误");
}
    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.add(user);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if ("ADMIN".equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if ("USER".equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

}
