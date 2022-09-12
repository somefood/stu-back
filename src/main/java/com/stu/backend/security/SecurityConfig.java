package com.stu.backend.security;

import com.stu.backend.security.jwt.JwtAccessDeniedHandler;
import com.stu.backend.security.jwt.JwtAuthenticationEntryPoint;
import com.stu.backend.security.jwt.JwtAuthenticationFilter;
import com.stu.backend.security.oauth.CookieAuthorizationRequestRepository;
import com.stu.backend.security.oauth.CustomOAuth2UserService;
import com.stu.backend.security.oauth.OAuth2AuthenticationFailureHandler;
import com.stu.backend.security.oauth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
	private final OAuth2AuthenticationSuccessHandler authenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler authenticationFailureHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/favicon.ico");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors()
				.and()
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.formLogin().disable()
				.httpBasic().disable();

		http.authorizeRequests()
				.mvcMatchers("/login", "/logout", "/oauth2/**").permitAll()
				.anyRequest().authenticated();

		http.oauth2Login()
				.authorizationEndpoint()
				.baseUri("/oauth2/authorize")
				.authorizationRequestRepository(cookieAuthorizationRequestRepository)
				.and()
				.redirectionEndpoint()
				.baseUri("/oauth2/callback/*")
				.and()
				.userInfoEndpoint()
				.userService(customOAuth2UserService)
				.and()
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler);

		http.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401
				.accessDeniedHandler(jwtAccessDeniedHandler); // 403

		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
