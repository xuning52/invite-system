package com.example.invite_system.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录态：token -> 用户 id，放在内存里。
 *
 * <p>后台重启后 token 失效，需要重新登录；用户数据本身在 H2 文件库里不会丢。
 */
@Component
public class TokenStore {

	private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();
	private final SecureRandom random = new SecureRandom();

	public String issue(Long userId) {
		byte[] bytes = new byte[24];
		random.nextBytes(bytes);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		tokenToUserId.put(token, userId);
		return token;
	}

	public Optional<Long> lookup(String token) {
		if (token == null || token.isBlank()) {
			return Optional.empty();
		}
		return Optional.ofNullable(tokenToUserId.get(token));
	}

	public void revoke(String token) {
		if (token != null) {
			tokenToUserId.remove(token);
		}
	}
}
