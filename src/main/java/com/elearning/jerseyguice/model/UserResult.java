package com.elearning.jerseyguice.model;

public class UserResult {
	private Integer id;
	private Long userId;
	private Integer hsk;
	private Integer testLesson;
	private Long totalScore;
	private Long totalTime;
	private Long totalListenScore;
	private Long totalListenTime;
	private Long totalReadingScore;
	private Long totalReadingTime;
	private String resultDetail;
	private Integer wordAmount;
	private String wordDetail;
	private String resultType;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getHsk() {
		return hsk;
	}
	public void setHsk(Integer hsk) {
		this.hsk = hsk;
	}
	public Integer getTestLesson() {
		return testLesson;
	}
	public void setTestLesson(Integer testLesson) {
		this.testLesson = testLesson;
	}
	public Long getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(Long totalScore) {
		this.totalScore = totalScore;
	}
	public Long getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}
	public Long getTotalListenScore() {
		return totalListenScore;
	}
	public void setTotalListenScore(Long totalListenScore) {
		this.totalListenScore = totalListenScore;
	}
	public Long getTotalListenTime() {
		return totalListenTime;
	}
	public void setTotalListenTime(Long totalListenTime) {
		this.totalListenTime = totalListenTime;
	}
	public Long getTotalReadingScore() {
		return totalReadingScore;
	}
	public void setTotalReadingScore(Long totalReadingScore) {
		this.totalReadingScore = totalReadingScore;
	}
	public Long getTotalReadingTime() {
		return totalReadingTime;
	}
	public void setTotalReadingTime(Long totalReadingTime) {
		this.totalReadingTime = totalReadingTime;
	}
	public String getResultDetail() {
		return resultDetail;
	}
	public void setResultDetail(String resultDetail) {
		this.resultDetail = resultDetail;
	}
	public Integer getWordAmount() {
		return wordAmount;
	}
	public void setWordAmount(Integer wordAmount) {
		this.wordAmount = wordAmount;
	}
	public String getWordDetail() {
		return wordDetail;
	}
	public void setWordDetail(String wordDetail) {
		this.wordDetail = wordDetail;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	
}
