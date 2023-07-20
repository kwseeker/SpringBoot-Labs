package cn.iocoder.springcloud.labx14.springmvcdemo.controller;

import cn.iocoder.springcloud.labx14.springmvcdemo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/get")
    public String get(@RequestParam("id") Integer id) {
        return userService.getUser(id);
    }

}
