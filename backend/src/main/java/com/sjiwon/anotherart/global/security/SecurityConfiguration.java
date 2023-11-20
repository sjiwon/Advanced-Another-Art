package com.sjiwon.anotherart.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.filter.AjaxAuthenticationFilter;
import com.sjiwon.anotherart.global.security.filter.InvalidTokenExceptionTranslationFilter;
import com.sjiwon.anotherart.global.security.filter.JwtAuthorizationFilter;
import com.sjiwon.anotherart.global.security.filter.LogoutExceptionTranslationFilter;
import com.sjiwon.anotherart.global.security.filter.RequestResponseCachingFilter;
import com.sjiwon.anotherart.global.security.handler.AjaxAuthenticationFailureHandler;
import com.sjiwon.anotherart.global.security.handler.AjaxAuthenticationSuccessHandler;
import com.sjiwon.anotherart.global.security.handler.JwtAccessDeniedHandler;
import com.sjiwon.anotherart.global.security.handler.JwtAuthenticationEntryPoint;
import com.sjiwon.anotherart.global.security.handler.JwtLogoutSuccessHandler;
import com.sjiwon.anotherart.global.security.properties.CorsProperties;
import com.sjiwon.anotherart.global.security.provider.AjaxAuthenticationProvider;
import com.sjiwon.anotherart.global.security.provider.CustomUserDetailsService;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.token.domain.service.TokenIssuer;
import com.sjiwon.anotherart.token.utils.TokenProvider;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOriginPatterns(corsProperties.getAllowedOriginPatterns());
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "HEAD", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", corsConfiguration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(memberRepository);
    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider(userDetailsService(), passwordEncoder());
    }

    @Bean
    public AuthenticationManager ajaxAuthenticationManager() throws Exception {
        final ProviderManager authenticationManager = (ProviderManager) authenticationConfiguration.getAuthenticationManager();
        authenticationManager.getProviders().add(ajaxAuthenticationProvider());
        return authenticationManager;
    }

    @Bean
    public AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler(tokenIssuer, tokenResponseWriter, objectMapper);
    }

    @Bean
    public AuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler(objectMapper);
    }

    @Bean
    public AjaxAuthenticationFilter ajaxAuthenticationFilter() throws Exception {
        final AjaxAuthenticationFilter authenticationFilter = new AjaxAuthenticationFilter(objectMapper);
        authenticationFilter.setAuthenticationManager(ajaxAuthenticationManager());
        authenticationFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler());
        return authenticationFilter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(tokenProvider, memberRepository);
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
    public InvalidTokenExceptionTranslationFilter tokenInvalidExceptionTranslationFilter() {
        return new InvalidTokenExceptionTranslationFilter(jwtAccessDeniedHandler());
    }

    @Bean
    public JwtLogoutSuccessHandler jwtLogoutSuccessHandler() {
        return new JwtLogoutSuccessHandler(tokenProvider, tokenIssuer);
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
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**");
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
        );

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.addFilterBefore(logoutExceptionTranslationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthorizationFilter(), LogoutExceptionTranslationFilter.class);
        http.addFilterBefore(tokenInvalidExceptionTranslationFilter(), JwtAuthorizationFilter.class);
        http.addFilterAt(ajaxAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(requestResponseCachingFilter(), AuthorizationFilter.class);

        http.logout(logout ->
                logout.logoutUrl("/api/logout")
                        .clearAuthentication(true)
                        .logoutSuccessHandler(jwtLogoutSuccessHandler())
        );

        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint())
                        .accessDeniedHandler(jwtAccessDeniedHandler())
        );

        return http.build();
    }
}
