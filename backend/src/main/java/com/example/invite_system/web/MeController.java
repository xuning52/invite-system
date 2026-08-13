package com.example.invite_system.web;

import com.example.invite_system.domain.User;
import com.example.invite_system.service.ApiException;
import com.example.invite_system.service.TokenStore;
import com.example.invite_system.service.UserService;
import com.example.invite_system.web.dto.ProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 个人主页数据：自己的邀请码、积分，以及被自己邀请来的用户列表。 */
@RestController
@RequestMapping("/api/me")
public class MeController {

	private final UserService userService;
	private final TokenStore tokenStore;

	public MeController(UserService userService, TokenStore tokenStore) {
		this.userService = userService;
		this.tokenStore = tokenStore;
	}

	@GetMapping
	public ProfileResponse me(@RequestHeader(value = "Authorization", required = false) String authorization) {
		Long userId = tokenStore.lookup(BearerToken.from(authorization))
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "登录状态已失效，请重新登录"));
		User user = userService.requireById(userId);
		return userService.profileOf(user);
	}
}
