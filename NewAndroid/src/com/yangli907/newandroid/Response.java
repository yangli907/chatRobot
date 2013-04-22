package com.yangli907.newandroid;

public class Response {
	private boolean left;

	private String message;
	public Response(boolean left, String message) {
		super();
		this.left = left;
		this.message = message;
	}
	
	public boolean isLeft() {
		return left;
	}
	public void setLeft(boolean left) {
		this.left = left;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
