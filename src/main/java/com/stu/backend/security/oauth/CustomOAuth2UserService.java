package com.stu.backend.security.oauth;

import com.stu.backend.domain.AuthProvider;
import com.stu.backend.domain.Role;
import com.stu.backend.domain.Status;
import com.stu.backend.domain.User;
import com.stu.backend.exception.OAuthProcessingException;
import com.stu.backend.repository.UserRepository;
import com.stu.backend.security.CustomUserDetails;
import com.stu.backend.security.oauth.user.OAuth2UserInfo;
import com.stu.backend.security.oauth.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;

	// OAuth2UserRequest에 있는 Access Token으로 유저정보 get
	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
		log.info("load user email = {}", oAuth2User.getName());

		return process(oAuth2UserRequest, oAuth2User);
	}

	// 획득한 유저정보를 Java Model과 맵핑하고 프로세스 진행
	private OAuth2User process(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		AuthProvider authProvider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());

		if (userInfo.getEmail().isEmpty()) {
			throw new OAuthProcessingException("Email not found from OAuth2 provider");
		}
		Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
		User user;

		if (userOptional.isPresent()) {        // 이미 가입된 경우
			user = userOptional.get();
			if (authProvider != user.getAuthProvider()) {
				throw new OAuthProcessingException("Wrong Match Auth Provider");
			}

		} else {            // 가입되지 않은 경우
			user = createUser(userInfo, authProvider);
		}
		return CustomUserDetails.create(user, oAuth2User.getAttributes());
	}

	private User createUser(OAuth2UserInfo userInfo, AuthProvider authProvider) {
		User user = User.builder()
				.email(userInfo.getEmail())
				.role(Role.USER)
				.status(Status.ACTIVE)
				.authProvider(authProvider)
				.build();
		return userRepository.save(user);
	}
}
