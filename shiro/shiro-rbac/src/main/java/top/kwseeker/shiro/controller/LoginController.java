package top.kwseeker.shiro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/captcha")
public class LoginController {

    /**
     * 传统验证码
     */
    @GetMapping("/get")
    public void get(HttpServletRequest request, HttpServletResponse response) {
        
    }
}
