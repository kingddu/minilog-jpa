package com.asdf.minilog.config;


import com.asdf.minilog.security.JwtAuthenticationEntryPoint;
import com.asdf.minilog.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtRequestFilter jwtRequestFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(
            AuthenticationConfiguration configuration)
        throws Exception {
            return configuration.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws
        Exception {
            httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(

                            // 주석 부분이 원래 소스인데 스웨거 접속하기 위해 바꿈 ...
                            /*
                            (requests) ->
                                    requests
                                            .requestMatchers("/api/v2/auth/login", "/swagger-ui/**",
                                                    "/v3/api-docs/**")
                                            .permitAll()
                            //사용자 생성, 조회는 인증 없이 가능
                                            .requestMatchers(HttpMethod.POST, "/api/v2/user")
                                            .permitAll()
                                            .requestMatchers(HttpMethod.GET, "/api/v2/user/{userId}")
                                            .permitAll()
                            //사용자 삭제는 ADMIN 권한이 필요
                                            .requestMatchers(HttpMethod.DELETE, "/api/v2/user/" +
                                                    "{userId}")
                                            .hasRole("ADMIN")
                                            .anyRequest()
                                            .authenticated()
                    */

                            auth -> auth
                                    // 🔥 로그인 허용
                                    .requestMatchers("/api/v2/auth/**").permitAll()

                                    // 🔥 회원가입 허용
                                    .requestMatchers(HttpMethod.POST, "/api/v2/user").permitAll()

                                    // 🔥 Swagger 허용
                                    .requestMatchers("/swagger-ui/**").permitAll()
                                    .requestMatchers("/swagger-ui.html").permitAll()
                                    .requestMatchers("/v3/api-docs/**").permitAll()
                                    .requestMatchers("/swagger-resources/**").permitAll()
                                    .requestMatchers("/webjars/**").permitAll()
                                    .requestMatchers("/error").permitAll()

                                    // 나머지는 인증 필요
                                    .anyRequest().authenticated()
                    )
                    .exceptionHandling(
                            exceptionHandling ->
                                    exceptionHandling.authenticationEntryPoint(
                                            jwtAuthenticationEntryPoint))
                    .sessionManagement(
                            sessionManagement ->
                                    sessionManagement.sessionCreationPolicy(
                                            SessionCreationPolicy.STATELESS));
            httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
            return httpSecurity.build();
        }
    }






