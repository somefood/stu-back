package com.stu.backend.security.oauth;

import com.stu.backend.exception.BadRequestException;
import com.stu.backend.security.jwt.JwtTokenProvider;
import com.stu.backend.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.stu.backend.security.oauth.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${app.oauth2.authorizedRedirectUri}")
	private String redirectUri;
	private final JwtTokenProvider tokenProvider;
	private final CookieAuthorizationRequestRepository authorizationRequestRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			log.debug("Response has already been committed");
			return;
		}
		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue);

		log.info("redirect uri = {}", redirectUri.get());
		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new BadRequestException("redirect URIs are not matched");
		}
		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		// JWT ??????
		String accessToken = tokenProvider.createAccessToken(authentication);
		tokenProvider.createRefreshToken(authentication, response);

		String url = UriComponentsBuilder.fromUriString(targetUrl)
				.queryParam("accessToken", accessToken)
				.build().toUriString();
		log.info("target url = {}", url);

		return url;
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);
		URI authorizedUri = URI.create(redirectUri);

		if (authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
				&& authorizedUri.getPort() == clientRedirectUri.getPort()) {
			return true;
		}

		return false;
	}
}
