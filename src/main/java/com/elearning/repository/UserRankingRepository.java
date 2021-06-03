package com.elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elearning.entity.UserRanking;
import com.elearning.entity.UserResult;

@Repository
public interface UserRankingRepository extends JpaRepository<UserRanking, Long> {
	List<UserRanking> findByUserId(Long userId);
}
