package com.xueershangda.oauth2js.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Spring Security 用户服务
 *
 * @author yinlei
 * @since 2019-8-23 13:40
 */
public class UserDetailServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = new User();
        user.setEnabled(true);
        user.setPassword("123456");
        user.setUsername("yinlei");
        return user;
    }
}
