package com.elearning.model;

public class RankingLevel {
	private long userId;
	private int validChain;
	private int level;
	private String name;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getValidChain() {
		return validChain;
	}
	public void setValidChain(int validChain) {
		this.validChain = validChain;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
