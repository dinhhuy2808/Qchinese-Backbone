package com.elearning.model;

public class FindUserByPhoneResponse {

	private String userKey;
	private String name;
	private Boolean isFriend;
	private String avatar;
	
	public FindUserByPhoneResponse(String userKey, String name, Boolean isFriend, String avatar) {
		this.userKey = userKey;
		this.name = name;
		this.isFriend = isFriend;
		this.avatar = avatar;
	}
	public String getUserKey() {
		return userKey;
	}
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsFriend() {
		return isFriend;
	}
	public void setIsFriend(Boolean isFriend) {
		this.isFriend = isFriend;
	}
}
