package com.example.invite_system.web;

import com.example.invite_system.domain.User;
import com.example.invite_system.service.TokenStore;
import com.example.invite_system.service.UserService;
import com.example.invite_system.web.dto.AuthResponse;
import com.example.invite_system.web.dto.LoginRequest;
import com.example.invite_system.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;
	private final TokenStore tokenStore;

	public AuthController(UserService userService, TokenStore tokenStore) {
		this.userService = userService;
		this.tokenStore = tokenStore;
	}

	/** 注册成功后直接发 token，前端可以不用再跳登录页。 */
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		User user = userService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new AuthResponse(tokenStore.issue(user.getId()), user.getUsername()));
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		User user = userService.login(request);
		return new AuthResponse(tokenStore.issue(user.getId()), user.getUsername());
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
		tokenStore.revoke(BearerToken.from(authorization));
		return ResponseEntity.noContent().build();
	}
}
