package com.coderscampus.AssignmentSubmissionApp.config;

import com.coderscampus.AssignmentSubmissionApp.filter.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint((request, response, ex) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage()))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


/*
Этот код конфигурирует безопасность в Spring Boot следующим образом: 1️⃣ Запрос проходит через JwtFilter → Если есть токен, проверяем его.
2️⃣ CSRF и CORS отключены, потому что используем JWT.
3️⃣ Сессии нет, потому что всё хранится в токене.
4️⃣ Эндпоинты /api/auth/** доступны всем, остальные требуют аутентификации.
5️⃣ Пароли хранятся в зашифрованном виде (BCrypt).




📌 Конкретные сценарии, когда он используется
1️⃣ При запуске приложения
Когда Spring Boot запускается, он загружает все @Configuration классы, включая SecurityConfig.

Это активирует Spring Security и применяет все настройки.
После запуска:
✅ Любой запрос будет проверяться системой безопасности.
✅ Без JWT-токена запросы, кроме /api/auth/**, будут отклоняться.
2️⃣ При отправке запроса на защищённые эндпоинты
Допустим, есть два API-метода:

POST /api/auth/login – логин пользователя.
GET /api/assignments – получение списка заданий (требует аутентификации).
📌 Как это работает?
✅ Шаг 1: Логин пользователя (/api/auth/login)

Пользователь отправляет логин и пароль.
Сервер проверяет их, создаёт JWT-токен и отправляет в ответе.
✅ Шаг 2: Доступ к защищённому API (/api/assignments)

Теперь пользователь должен отправлять JWT-токен в заголовке запроса:
http
Копировать
Редактировать
GET /api/assignments
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Что делает SecurityConfig в этот момент?
Запрос проходит через JwtFilter → проверяем токен.
Если токен валиден, то пользователь аутентифицирован.
Spring Security пропускает запрос к GET /api/assignments.
Если токен отсутствует или неверный, запрос блокируется и возвращается 401 Unauthorized.
 */

