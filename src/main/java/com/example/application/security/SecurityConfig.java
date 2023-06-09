
package com.example.application.security;

import com.example.application.data.entity.Users;
import com.example.application.data.services.UserService;
import com.example.application.views.loginview.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {


    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/images/*.png").permitAll();
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    public UserDetailsService users() {

        List<UserDetails> userDetails = new ArrayList<>();
        List<Users> users = userService.findAll();

        for(Users u : users)
        {
            UserDetails user1 = User.builder()
                    .username(u.getUsername())
                    .password("{noop}"+u.getPassword())
                    .roles(String.valueOf(u.getUserRole()))
                    .build();

            userDetails.add(user1);
        }
        return new InMemoryUserDetailsManager(userDetails);

    }
}