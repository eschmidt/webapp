package com.sharedmeals.security;

public class DuplicateEmailException extends Exception {
	public DuplicateEmailException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -2071233233619060266L;
}
