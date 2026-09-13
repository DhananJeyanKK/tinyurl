package com.learn.tinyurl.security;

import com.learn.tinyurl.exceptionhandling.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        //when maximum session reached it doesnt allow to login - if we remove flag it allows
        //session fixation - hacker uses same session id and send to others make them login so same session id gets all access
        http.sessionManagement(sm -> sm.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                .invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true));
        http.csrf(AbstractHttpConfigurer::disable);//We disable this because of Permit all gives CSRF exception for post but get works.
        http.authorizeHttpRequests(re -> re.requestMatchers("/url", "/{shortKey}").authenticated()
                .requestMatchers( "/customer/register", "/invalidSession", "/logout").permitAll());
        http.formLogin(Customizer.withDefaults());
        http.httpBasic(aep -> aep.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
        //http.logout(l->l.logoutSuccessUrl("/logout").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
