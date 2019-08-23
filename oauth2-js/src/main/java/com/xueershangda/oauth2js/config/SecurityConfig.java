package com.xueershangda.oauth2js.config;

import com.xueershangda.oauth2js.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 配置 Spring Security
 *
 * @author yinlei
 * @since 2019-8-23 13:39
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService simpleUserDetailsService(){
        return new UserDetailServiceImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(simpleUserDetailsService());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.userDetailsService(userDetailsService());
        http.csrf().disable();
        http.formLogin()
                .loginPage("/signin").loginProcessingUrl("/signin/form/account").defaultSuccessUrl("/index")
                .and()
                .logout().logoutUrl("/signout").logoutSuccessUrl("/signin")
                .and()
                .authorizeRequests()
                .antMatchers("/signin","/signin/form/tel","/code/image","/code/mobile","/static/**").permitAll()
                .antMatchers("/oauth/**").permitAll()
                .antMatchers("/user/**").hasAnyRole("USER","ADMIN")
                .anyRequest().authenticated();

    }

}
