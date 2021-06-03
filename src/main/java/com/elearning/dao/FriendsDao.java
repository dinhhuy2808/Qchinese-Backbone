package com.elearning.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.elearning.model.FriendsResponse;

@Component
public interface FriendsDao {
	List<FriendsResponse> getFriendsDetail(Long userId);
	List<FriendsResponse> getFriendRequest(Long userId);
	boolean createFriendsRequest(Long userId, Long friendId, String roomKey);
}
