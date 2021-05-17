package com.elearning.jerseyguice.model;

public class UserDictionary {
	private Long userId;
	private String wordId;
	private Integer createDate;
	private String tab;
	private String place;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getWordId() {
		return wordId;
	}
	public void setWordId(String wordId) {
		this.wordId = wordId;
	}
	public Integer getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Integer createDate) {
		this.createDate = createDate;
	}
	public String getTab() {
		return tab;
	}
	public void setTab(String tab) {
		this.tab = tab;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
}
