package com.elearning.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter @Data
public class FriendsResponse {
	private Long userId;
	private String roomKey;
	private String roomName;
	private String avatar;
	private Long unread;
	private String userKey;
	private Boolean isAccept;
}
