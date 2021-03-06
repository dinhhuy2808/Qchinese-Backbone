package com.elearning.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.elearning.filtes.JWTAuthenticationFilter;
import com.elearning.filtes.JWTLoginFilter;
import com.elearning.filtes.JwtUtil;

import javax.sql.DataSource;

/**
 * Created by nhs3108 on 29/03/2017.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/*").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .and()
				/*
				 * .addFilterBefore(jwtLoginFilter(),
				 * UsernamePasswordAuthenticationFilter.class)
				 */
                .addFilterBefore(new JWTAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JWTLoginFilter jwtLoginFilter() throws Exception {
    	return new JWTLoginFilter("/login", authenticationManager());
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("username").password("abc").roles("ADMIN");
        
        // M??nh comment ph???n d?????i n??y v?? ch??ng ta ko s??? d???ng DB nh??. N???u c??c b???n s??? d???ng, b??? comment v?? config query sao cho ph?? h???p. C??c b???n c?? th??? GG ????? t??m hi???u th??m
//        auth.jdbcAuthentication().dataSource(dataSource)
//                .usersByUsernameQuery("select username,password, 1 from user where phone=?")
//                .authoritiesByUsernameQuery("select username, 1 from user where phone=?");
        
    }
}
