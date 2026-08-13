package com.example.invite_system.web.dto;

import java.time.Instant;
import java.util.List;

/** 个人主页需要的全部数据。 */
public record ProfileResponse(
		String username,
		String inviteCode,
		int points,
		Instant createdAt,
		/** 我邀请来的人，按注册时间从早到晚。 */
		List<Invitee> invitees) {

	public record Invitee(String username, Instant registeredAt) {
	}
}
