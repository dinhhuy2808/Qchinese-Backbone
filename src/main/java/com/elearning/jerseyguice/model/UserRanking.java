package com.elearning.jerseyguice.model;

public class UserRanking {
	private Long userId;
	private Long totalScore;
	private Integer titleId;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(Long totalScore) {
		this.totalScore = totalScore;
	}
	public Integer getTitleId() {
		return titleId;
	}
	public void setTitleId(Integer titleId) {
		this.titleId = titleId;
	}
}
