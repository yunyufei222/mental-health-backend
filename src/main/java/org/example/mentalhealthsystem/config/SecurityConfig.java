package org.example.mentalhealthsystem.config;

import org.example.mentalhealthsystem.util.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 公开的 GET 请求
                        .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/scales/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/counselors/**").permitAll()      // 咨询师列表和排班查询
                        .requestMatchers(HttpMethod.GET, "/api/community/posts/**").permitAll() // 帖子列表、详情、评论列表

                        // 登录注册完全公开
                        .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                        .requestMatchers("/api/public/**").permitAll()

                        // 社区写操作（发帖、评论、点赞等）需要登录（任何角色）
                        .requestMatchers(HttpMethod.POST, "/api/community/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/community/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/community/posts/**").authenticated()
                        .requestMatchers("/api/community/**").authenticated()

                        // 学习记录、预约等需要登录
                        .requestMatchers("/api/user/reads/**").authenticated()
                        .requestMatchers("/api/appointments/**").authenticated()

                        // 管理员专用
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 其他请求默认需要认证
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}