package com.example.invite_system.service;

import org.springframework.http.HttpStatus;

/** 业务校验失败：带上要返回给前端的状态码和中文提示。 */
public class ApiException extends RuntimeException {

	private final HttpStatus status;

	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
