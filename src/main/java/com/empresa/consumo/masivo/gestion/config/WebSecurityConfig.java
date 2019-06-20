package com.empresa.consumo.masivo.gestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

import static java.util.Arrays.asList;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        
        http.authorizeRequests()
            .antMatchers("/registroUsuario.html","/resetPassword.html","/swagger-ui.html/**", "/api/swagger-ui.html/**", "/webjars/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
            .and()
            .headers().frameOptions().sameOrigin();

        http.csrf().disable()
            .cors()
            .and()
            .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers(HttpMethod.POST, "/api/usuario/login").permitAll()
            .antMatchers(HttpMethod.POST, "/api/usuario/register").authenticated()
            .antMatchers( "/api/usuario/file/upload").authenticated()
            .antMatchers( "/api/usuario/file/delete").authenticated()
            .antMatchers( "/api/usuario/file/download").permitAll()
            .antMatchers(HttpMethod.POST, "/api/productos/add").authenticated()
            .antMatchers(HttpMethod.GET, "/api/productos/all").authenticated()
            .antMatchers(HttpMethod.PUT, "/api/productos/update").authenticated()
            .antMatchers(HttpMethod.DELETE, "/api/productos/delete").authenticated()
            .antMatchers( "/api/productos/file/upload").authenticated()
            .antMatchers( "/api/productos/file/delete").authenticated()
            .antMatchers( "/api/productos/file/download").permitAll()
            .antMatchers(HttpMethod.POST, "/api/materiales/add").authenticated()
            .antMatchers(HttpMethod.GET, "/api/materiales/all").authenticated()
            .antMatchers(HttpMethod.PUT, "/api/materiales/update").authenticated()
            .antMatchers(HttpMethod.DELETE, "/api/materiales/delete").authenticated()
            .antMatchers( "/api/materiales/file/upload").authenticated()
            .antMatchers( "/api/materiales/file/delete").authenticated()
            .antMatchers( "/api/materiales/file/download").permitAll()
            .antMatchers( "/api/productoMaterial/**").authenticated()
            .anyRequest().authenticated();

        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(asList("HEAD",
            "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(asList("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
