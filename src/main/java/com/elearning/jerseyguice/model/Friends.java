package com.elearning.jerseyguice.model;

import java.util.Date;

public class Friends {
	private Long id;
	private Long userId;
	private Long friendId;
	private String friendNickName;
	private Boolean isAccepped;
	private Date createdDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getFriendId() {
		return friendId;
	}
	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}
	public String getFriendNickName() {
		return friendNickName;
	}
	public void setFriendNickName(String friendNickName) {
		this.friendNickName = friendNickName;
	}
	public Boolean getIsAccepped() {
		return isAccepped;
	}
	public void setIsAccepped(Boolean isAccepped) {
		this.isAccepped = isAccepped;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
