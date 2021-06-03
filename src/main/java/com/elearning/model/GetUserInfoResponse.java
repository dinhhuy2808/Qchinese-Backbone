package com.elearning.model;

import java.util.List;
import java.util.Map;

import com.elearning.entity.User;
import com.elearning.entity.UserResult;

public class GetUserInfoResponse {
	private User user;
	private List<QuizResultResponse> quizResultDetail;
	private List<QuizResultResponse> testResultDetail;
	private List<UserResult> totalResults;
	private List<Rank> ranks;
	private List<Long> friendIds;
	private int userCurrentRank;
	private Map<Integer, List<RankingLevel>> rankingLevel;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public List<QuizResultResponse> getTestResultDetail() {
		return testResultDetail;
	}
	public void setTestResultDetail(List<QuizResultResponse> testResultDetail) {
		this.testResultDetail = testResultDetail;
	}
	public List<QuizResultResponse> getQuizResultDetail() {
		return quizResultDetail;
	}
	public void setQuizResultDetail(List<QuizResultResponse> quizResultDetail) {
		this.quizResultDetail = quizResultDetail;
	}
	public List<UserResult> getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(List<UserResult> totalResults) {
		this.totalResults = totalResults;
	}
	public List<Rank> getRanks() {
		return ranks;
	}
	public void setRanks(List<Rank> ranks) {
		this.ranks = ranks;
	}
	public List<Long> getFriendIds() {
		return friendIds;
	}
	public void setFriendIds(List<Long> friendIds) {
		this.friendIds = friendIds;
	}
	public int getUserCurrentRank() {
		return userCurrentRank;
	}
	public void setUserCurrentRank(int userCurrentRank) {
		this.userCurrentRank = userCurrentRank;
	}
	public Map<Integer, List<RankingLevel>> getRankingLevel() {
		return rankingLevel;
	}
	public void setRankingLevel(Map<Integer, List<RankingLevel>> rankingLevel) {
		this.rankingLevel = rankingLevel;
	}
	
}
