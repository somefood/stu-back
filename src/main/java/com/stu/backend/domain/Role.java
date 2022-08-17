package com.stu.backend.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	ADMIN("ROLE_ADMIN", "admin"),
	USER("ROLE_USER", "user");

	private final String role;
	private final String name;
}
