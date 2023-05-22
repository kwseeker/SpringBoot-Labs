package top.kwseeker.springboot.lab04.springsecurity.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Component
public class SecurityUserDetailsService implements UserDetailsService {

    @Resource
    private SqlSession sqlSession;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Map<String, Object> userInfoMap = sqlSession.selectOne("Login.loadUserByUsername", username);
            SecurityUserDetails user = new SecurityUserDetails(username, MapUtils.getString(userInfoMap, "nick_name"),
                    MapUtils.getString(userInfoMap, "bcrypt_passwd"), new ArrayList<>(),
                    true, true, true, true);
            log.info("登录用户信息：{}", user);
            return user;
        } catch (Exception e) {
            String msg = "Username: " + username + " not found";
            log.error(msg, e);
            throw new UsernameNotFoundException(msg);
        }
    }
}
