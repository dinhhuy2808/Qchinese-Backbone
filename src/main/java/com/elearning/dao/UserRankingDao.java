package com.elearning.dao;

import java.util.List;
import java.util.Map;

import com.elearning.entity.UserResult;
import com.elearning.model.Rank;
import com.elearning.model.RankingLevel;

public interface UserRankingDao  {
	 int getTotalScoreBy(Long userId);
	 int getCurrentRankBy(Long userId);
	 boolean isEligibleForPromoteTesting(int userId, int currentHsk);
	 boolean isEligibleForPromotion(int userId, int currentHsk);
	 List<UserResult> getTotalResultBy(Long userId);
	 List<Rank> getRankBoard();
	 Map<Integer, List<RankingLevel>> getCurrentRankLevel(int currentRank);
}