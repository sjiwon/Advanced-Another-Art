package com.sjiwon.anotherart.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.filter.RequestResponseCachingFilter;
import com.sjiwon.anotherart.global.security.filter.JsonAuthenticationFilter;
import com.sjiwon.anotherart.global.security.filter.JwtAuthorizationExceptionTranslationFilter;
import com.sjiwon.anotherart.global.security.filter.JwtAuthorizationFilter;
import com.sjiwon.anotherart.global.security.filter.LogoutExceptionTranslationFilter;
import com.sjiwon.anotherart.global.security.handler.JsonAuthenticationFailureHandler;
import com.sjiwon.anotherart.global.security.handler.JsonAuthenticationSuccessHandler;
import com.sjiwon.anotherart.global.security.handler.JwtAccessDeniedHandler;
import com.sjiwon.anotherart.global.security.handler.JwtAuthenticationEntryPoint;
import com.sjiwon.anotherart.global.security.handler.JwtLogoutSuccessHandler;
import com.sjiwon.anotherart.global.security.handler.JwtLogoutTokenCheckHandler;
import com.sjiwon.anotherart.global.security.properties.CorsProperties;
import com.sjiwon.anotherart.global.security.provider.JsonAuthenticationProvider;
import com.sjiwon.anotherart.global.security.provider.RdbUserDetailsService;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.domain.service.TokenIssuer;
import com.sjiwon.anotherart.token.domain.service.TokenProvider;
import com.sjiwon.anotherart.token.utils.TokenResponseWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CorsProperties corsProperties;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final TokenIssuer tokenIssuer;
    private final TokenResponseWriter tokenResponseWriter;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(corsProperties.getAllowedOriginPatterns());
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "HEAD", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", corsConfiguration);
        return source;
    }

    @Bean
    public UserDetailsService rdbUserDetailsService() {
        return new RdbUserDetailsService(memberRepository);
    }

    @Bean
    public AuthenticationProvider jsonAuthenticationProvider() {
        return new JsonAuthenticationProvider(rdbUserDetailsService(), passwordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        final ProviderManager authenticationManager = (ProviderManager) authenticationConfiguration.getAuthenticationManager();
        authenticationManager.getProviders().add(jsonAuthenticationProvider());
        return authenticationManager;
    }

    @Bean
    public AuthenticationSuccessHandler jsonAuthenticationSuccessHandler() {
        return new JsonAuthenticationSuccessHandler(tokenIssuer, tokenResponseWriter, objectMapper);
    }

    @Bean
    public AuthenticationFailureHandler jsonAuthenticationFailureHandler() {
        return new JsonAuthenticationFailureHandler(objectMapper);
    }

    @Bean
    public JsonAuthenticationFilter jsonAuthenticationFilter() throws Exception {
        final JsonAuthenticationFilter authenticationFilter = new JsonAuthenticationFilter(objectMapper);
        authenticationFilter.setAuthenticationManager(authenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(jsonAuthenticationSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(jsonAuthenticationFailureHandler());
        return authenticationFilter;
    }

    @Bean
    public AuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public AccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler(objectMapper);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(tokenProvider, memberRepository);
    }

    @Bean
    public JwtAuthorizationExceptionTranslationFilter jwtAuthorizationExceptionTranslationFilter() {
        return new JwtAuthorizationExceptionTranslationFilter(jwtAccessDeniedHandler());
    }

    @Bean
    public LogoutHandler jwtLogoutTokenCheckHandler() {
        return new JwtLogoutTokenCheckHandler();
    }

    @Bean
    public LogoutSuccessHandler jwtLogoutSuccessHandler() {
        return new JwtLogoutSuccessHandler(tokenIssuer);
    }

    @Bean
    public LogoutExceptionTranslationFilter logoutExceptionTranslationFilter() {
        return new LogoutExceptionTranslationFilter(jwtAccessDeniedHandler());
    }

    @Bean
    public RequestResponseCachingFilter requestResponseCachingFilter() {
        return new RequestResponseCachingFilter();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/swagger", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(request ->
                request
                        // Auth
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/logout").hasRole("USER")
                        .requestMatchers("/api/token/reissue").permitAll()

                        // Member
                        .requestMatchers("/api/members/me/**").hasRole("USER")
                        .requestMatchers("/api/members/**").permitAll()

                        // Art
                        .requestMatchers(HttpMethod.GET, "/api/arts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/arts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/arts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/arts/**").hasRole("USER")

                        // Auction
                        .requestMatchers("/api/auctions/**").hasRole("USER")

                        // Point
                        .requestMatchers("/api/points/**").hasRole("USER")

                        // Other
                        .anyRequest().authenticated()
        );

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.addFilterBefore(logoutExceptionTranslationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), LogoutExceptionTranslationFilter.class);
        http.addFilterBefore(jwtAuthorizationExceptionTranslationFilter(), JwtAuthorizationFilter.class);
        http.addFilterAt(jsonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(requestResponseCachingFilter(), AuthorizationFilter.class);

        http.logout(logout ->
                logout.logoutUrl("/api/logout")
                        .clearAuthentication(true)
                        .deleteCookies(AuthToken.REFRESH_TOKEN_HEADER)
                        .addLogoutHandler(jwtLogoutTokenCheckHandler())
                        .logoutSuccessHandler(jwtLogoutSuccessHandler())
        );

        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint())
                        .accessDeniedHandler(jwtAccessDeniedHandler())
        );

        return http.build();
    }
}
