package top.kwseeker.springboot.lab04.springsecurity.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kwseeker.springboot.lab04.springsecurity.Body;

import javax.annotation.Resource;
import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Resource
    private SqlSession sqlSession;

    @PostMapping(value = "/add_user")
    public ResponseEntity addUser(@Valid @RequestBody UserPojo userPojo) {
        log.debug("添加用户信息: {}", userPojo);
        userPojo.setBcryptPasswd(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(userPojo.getPassword()));
        try {
            int result = sqlSession.insert("User.addUser", userPojo);
            if (result == 1) {
                return ok(Body.build().ok("添加用户成功。", userPojo));
            } else {
                return ok(Body.build().fail("添加用户失败。"));
            }
        } catch (Exception e) {
            return ok(Body.build().fail("添加用户发生未知错误。" + e.getMessage()));
        }
    }

    @PostMapping(value = "/edit_user")
    public ResponseEntity editUser(@Valid @RequestBody UserPojo userPojo) {
        return ok(Body.build().ok("编辑用户信息成功！", userPojo));
    }

    @PostMapping(value = "/delete_user")
    public ResponseEntity deleteUser(@Valid @RequestBody UserPojo userPojo) {
        return ok(Body.build().ok("成功删除用户信息！", userPojo));
    }

}
