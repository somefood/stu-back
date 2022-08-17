package com.stu.backend.security.oauth.user;

import com.stu.backend.domain.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {
	public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
		switch (authProvider) {
			case GOOGLE:
				return new GoogleOAuth2UserInfo(attributes);
//			case GITHUB: return new GithubOAuth2UserInfo(attributes);
			default:
				throw new IllegalArgumentException("Invalid Provider Type.");
		}
	}
}
