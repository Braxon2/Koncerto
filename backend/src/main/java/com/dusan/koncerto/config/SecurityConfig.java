package com.dusan.koncerto.config;

import com.dusan.koncerto.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final AuthenticationProvider authProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authProvider = authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/buy").hasAuthority(Role.USER.getAuthority())
                        .requestMatchers(HttpMethod.POST, "/api/v1/events/*/image").hasAuthority(Role.ADMIN.getAuthority())
                        .requestMatchers(HttpMethod.POST, "/api/v1/tickets/**").hasAuthority(Role.USER.getAuthority())
                        .requestMatchers(HttpMethod.POST, "/api/v1/events").hasAnyAuthority(Role.ADMIN.getAuthority())

                        .requestMatchers(HttpMethod.GET, "/api/v1/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/events/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*/tickets").hasAuthority(Role.USER.getAuthority())
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/*").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/events/**").hasAnyAuthority(Role.ADMIN.getAuthority())

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/events/**").hasAnyAuthority(Role.ADMIN.getAuthority())

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/events/*/tickets/*").hasAuthority(Role.ADMIN.getAuthority())


                        .requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll()


                        .anyRequest().permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authProvider)
                .addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
