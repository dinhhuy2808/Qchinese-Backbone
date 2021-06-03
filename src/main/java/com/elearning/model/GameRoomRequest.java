package com.elearning.model;

import java.util.List;

import com.elearning.annotation.KeyColumn;
import com.elearning.constant.GameType;
import com.elearning.constant.LessonType;
import com.elearning.constant.Level;

public class GameRoomRequest {
	@KeyColumn
	private Long id;
	private String gameKey;
	private GameType gameType;
	private List<Integer> hsk;
	private List<Integer> lessons;
	private Level level;
	private Long hostId;
	private String createdDate;
	private String updatedDate;
	private String content;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGameKey() {
		return gameKey;
	}
	public void setGameKey(String gameKey) {
		this.gameKey = gameKey;
	}
	public GameType getGameType() {
		return gameType;
	}
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}
	public List<Integer> getHsk() {
		return hsk;
	}
	public void setHsk(List<Integer> hsk) {
		this.hsk = hsk;
	}
	public List<Integer> getLessons() {
		return lessons;
	}
	public void setLessons(List<Integer> lessons) {
		this.lessons = lessons;
	}
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public Long getHostId() {
		return hostId;
	}
	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
