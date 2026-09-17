package com.learn.tinyurl.security;

import com.learn.tinyurl.exceptionhandling.CustomAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        //when maximum session reached it doesnt allow to login - if we remove flag it allows
        //session fixation - hacker uses same session id and send to others make them login so same session id gets all access

        //Allows Cross origin resource sharing -> Calling from one domain to another domain.
        //Preflight request from browser checks for this and allow connection based on the below config.
//        http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
//            @Override
//            public @Nullable CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                CorsConfiguration corsConfiguration = new CorsConfiguration();
//
//                corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4000"));
//                corsConfiguration.setAllowCredentials(true);
//                corsConfiguration.setMaxAge(3600L);//remember for 1 hour
//
//                return corsConfiguration;
//            }
//        }));



        //Jession by default doesn't store now in Security Context. We have to manually do it.
        http.securityContext(sc -> sc.requireExplicitSave(false))//Whenever you set or update an Authentication object in the SecurityContextHolder during a request, Spring Security will automatically save it into that HttpSession when the request finishes—without requiring you to call a save method manually.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)//Session created for each session
                .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                .invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true));

        //This reads the token from the request - If we don't have then it doesn't read the csrf token.
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();


        //We disable this because of Permit all gives CSRF exception for post but get works.
        //Token generated only when the token is get accessed. It is generated lazily. You have to write logic to read token manually.
        http.csrf(csrf -> csrf.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                        .ignoringRequestMatchers("/customer/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                        .addFilterAfter(new CsrfTinyUrlFilter(), BasicAuthenticationFilter.class);

     //   http.authorizeHttpRequests(re -> re.requestMatchers("/url", "/{shortKey}").hasRole("customer"));

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
