package com.example.invite_system.web.dto;

/** 注册/登录成功后返回：前端拿 token 访问个人主页接口。 */
public record AuthResponse(String token, String username) {
}
