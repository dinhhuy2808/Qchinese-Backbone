package com.elearning.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.elearning.dao.FriendsDao;
import com.elearning.entity.Rooms;
import com.elearning.model.FriendsResponse;
import com.elearning.model.QuestionDescription;
import com.elearning.repository.FriendsRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class FriendsDaoImpl implements FriendsDao {
	private final FriendsRepository friendsRepository;
	
	@Override
	public List<FriendsResponse> getFriendsDetail(Long userId) {
		return friendsRepository.getFriendsDetail(userId);
	}

	@Override
	@Transactional
	public boolean createFriendsRequest(Long userId, Long friendId, String roomKey) {
		friendsRepository.insertFriends(userId, friendId, true);
		friendsRepository.insertFriends(friendId, userId, false);
		
		friendsRepository.insertRooms(userId, friendId, roomKey);
		friendsRepository.insertRooms(friendId, userId, roomKey);
		
		return false;
	}

	@Override
	public List<FriendsResponse> getFriendRequest(Long userId) {
		return friendsRepository.getFriendRequest(userId);
	}

}
