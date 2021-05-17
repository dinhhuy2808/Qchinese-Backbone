package com.elearning.jerseyguice.model;

public class Rank {
	private Long userId;
	private Long totalScore;
	private Integer titleId;
	private String userFullName;
	private Long wordAmount;
	private Integer hsk;
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
	public String getUserFullName() {
		return userFullName;
	}
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}
	public Long getWordAmount() {
		return wordAmount;
	}
	public void setWordAmount(Long wordAmount) {
		this.wordAmount = wordAmount;
	}
	public Integer getHsk() {
		return hsk;
	}
	public void setHsk(Integer hsk) {
		this.hsk = hsk;
	}
	
}
