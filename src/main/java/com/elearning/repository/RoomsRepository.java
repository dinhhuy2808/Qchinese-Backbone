package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.entity.Rooms;
import com.elearning.entity.UserResult;

@Repository
public interface RoomsRepository extends JpaRepository<Rooms, Long> {
	Long countByRoomKeyAndUserId(String roomKey, Long userId);
}
