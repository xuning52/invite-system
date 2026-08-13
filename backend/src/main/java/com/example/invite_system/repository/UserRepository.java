package com.example.invite_system.repository;

import com.example.invite_system.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);

	Optional<User> findByInviteCode(String inviteCode);

	boolean existsByInviteCode(String inviteCode);

	/** 某个用户邀请来的所有人，按注册时间从早到晚。 */
	List<User> findByInviterIdOrderByCreatedAtAsc(Long inviterId);
}
