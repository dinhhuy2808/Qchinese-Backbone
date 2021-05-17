package com.elearning.jerseyguice.model;

import java.util.Date;

public class Rooms {
	private Long id;
	private Long userId;
	private Boolean isGroup;
	private String roomName;
	private String roomKey;
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
	public Boolean getIsGroup() {
		return isGroup;
	}
	public void setIsGroup(Boolean isGroup) {
		this.isGroup = isGroup;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomKey() {
		return roomKey;
	}
	public void setRoomKey(String roomKey) {
		this.roomKey = roomKey;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
