package com.elearning.jerseyguice.model;

import java.util.List;

public class GetUserInfoResponse {
	User user;
	List<QuizResultResponse> quizResultDetail;
	List<QuizResultResponse> testResultDetail;
	List<UserResult> totalResults;
	List<Rank> ranks;
	List<Long> friendIds;
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
	
}
