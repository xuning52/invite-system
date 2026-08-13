package com.example.invite_system.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * 一个注册用户。
 *
 * <p>inviteCode 在注册时生成一次，之后不再改变；inviterId 记录是谁把这个用户拉进来的
 * （直接注册的用户为 null）。
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 用户名唯一，注册时重名会被拒绝。 */
	@Column(nullable = false, unique = true, length = 32)
	private String username;

	/** BCrypt 哈希，不存明文密码。 */
	@Column(nullable = false)
	private String passwordHash;

	/** 该用户自己的邀请码，注册时随机生成一次。 */
	@Column(nullable = false, unique = true, length = 16)
	private String inviteCode;

	/** 邀请人的用户 id，没有邀请人则为 null。 */
	@Column
	private Long inviterId;

	/** 每成功邀请一人 +10。 */
	@Column(nullable = false)
	private int points;

	@Column(nullable = false)
	private Instant createdAt;

	protected User() {
		// JPA 需要无参构造
	}

	public User(String username, String passwordHash, String inviteCode, Long inviterId) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.inviteCode = inviteCode;
		this.inviterId = inviterId;
		this.points = 0;
		this.createdAt = Instant.now();
	}

	public void addPoints(int delta) {
		this.points += delta;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public Long getInviterId() {
		return inviterId;
	}

	public int getPoints() {
		return points;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
