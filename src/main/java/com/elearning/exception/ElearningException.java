package com.elearning.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ElearningException extends Exception{

	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
}
