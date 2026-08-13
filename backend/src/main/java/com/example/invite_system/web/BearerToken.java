package com.example.invite_system.web;

/** 从 {@code Authorization: Bearer xxx} 请求头里取出 token。 */
final class BearerToken {

	private static final String PREFIX = "Bearer ";

	private BearerToken() {
	}

	static String from(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith(PREFIX)) {
			return null;
		}
		String token = authorizationHeader.substring(PREFIX.length()).trim();
		return token.isEmpty() ? null : token;
	}
}
