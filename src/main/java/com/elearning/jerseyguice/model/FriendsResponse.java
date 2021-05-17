package com.elearning.jerseyguice.model;

public class FriendsResponse {
	private Long userId;
	private String roomKey;
	private String roomName;
	private String avatar;
	private Long unread;
	
	public String getRoomKey() {
		return roomKey;
	}
	public void setRoomKey(String roomKey) {
		this.roomKey = roomKey;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Long getUnread() {
		return unread;
	}
	public void setUnread(Long unread) {
		this.unread = unread;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
