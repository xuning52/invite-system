package com.example.invite_system.service;

import com.example.invite_system.domain.User;
import com.example.invite_system.repository.UserRepository;
import com.example.invite_system.web.dto.LoginRequest;
import com.example.invite_system.web.dto.ProfileResponse;
import com.example.invite_system.web.dto.RegisterRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class UserService {

	/** 邀请码字符集：去掉了 0/O、1/I/l 这类抄起来容易看错的字符。 */
	private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	private static final int CODE_LENGTH = 8;

	/** 每成功邀请一人，邀请人得到的积分。 */
	private static final int INVITE_REWARD = 10;

	private final UserRepository users;
	private final PasswordEncoder passwordEncoder;
	private final SecureRandom random = new SecureRandom();

	public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User register(RegisterRequest request) {
		String username = request.username().trim();

		if (users.existsByUsername(username)) {
			throw new ApiException(HttpStatus.CONFLICT, "用户名「" + username + "」已被注册，请换一个用户名");
		}

		User inviter = resolveInviter(request.inviteCode());

		User user = new User(
				username,
				passwordEncoder.encode(request.password()),
				generateUniqueInviteCode(),
				inviter == null ? null : inviter.getId());
		try {
			user = users.save(user);
		}
		catch (DataIntegrityViolationException ex) {
			// 两个人同时提交同一个用户名时，靠数据库唯一约束兜底
			throw new ApiException(HttpStatus.CONFLICT, "用户名「" + username + "」已被注册，请换一个用户名");
		}

		if (inviter != null) {
			inviter.addPoints(INVITE_REWARD);
			users.save(inviter);
		}
		return user;
	}

	public User login(LoginRequest request) {
		return users.findByUsername(request.username().trim())
				.filter(user -> passwordEncoder.matches(request.password(), user.getPasswordHash()))
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));
	}

	public User requireById(Long id) {
		return users.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "登录状态已失效，请重新登录"));
	}

	@Transactional(readOnly = true)
	public ProfileResponse profileOf(User user) {
		List<ProfileResponse.Invitee> invitees = users.findByInviterIdOrderByCreatedAtAsc(user.getId())
				.stream()
				.map(invitee -> new ProfileResponse.Invitee(invitee.getUsername(), invitee.getCreatedAt()))
				.toList();

		return new ProfileResponse(
				user.getUsername(),
				user.getInviteCode(),
				user.getPoints(),
				user.getCreatedAt(),
				invitees);
	}

	/** 邀请码留空表示直接注册；填了就必须是真实存在的码。 */
	private User resolveInviter(String rawCode) {
		if (rawCode == null || rawCode.isBlank()) {
			return null;
		}
		String code = rawCode.trim().toUpperCase();
		return users.findByInviteCode(code)
				.orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "邀请码无效，请核对后重填，或留空直接注册"));
	}

	private String generateUniqueInviteCode() {
		for (int attempt = 0; attempt < 20; attempt++) {
			String code = randomCode();
			if (!users.existsByInviteCode(code)) {
				return code;
			}
		}
		throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "邀请码生成失败，请重试");
	}

	private String randomCode() {
		StringBuilder sb = new StringBuilder(CODE_LENGTH);
		for (int i = 0; i < CODE_LENGTH; i++) {
			sb.append(CODE_ALPHABET.charAt(random.nextInt(CODE_ALPHABET.length())));
		}
		return sb.toString();
	}
}
