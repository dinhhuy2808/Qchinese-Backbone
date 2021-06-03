package com.elearning.exceptionhandler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Builder;
import lombok.Data;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({IOException.class})
	@ResponseBody
	ErrorInfo handleIoException() {
		return ErrorInfo.builder().code(HttpStatus.FORBIDDEN.value()).build();
	}

	@Data
	@Builder
	public static class ErrorInfo {

		private int code;
		private String message;
		private Object errors;
	}
}
