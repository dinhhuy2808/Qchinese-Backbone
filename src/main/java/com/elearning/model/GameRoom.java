package com.elearning.model;

import java.util.List;

import com.elearning.annotation.KeyColumn;
import com.elearning.constant.GameType;
import com.elearning.constant.LessonType;
import com.elearning.constant.Level;

public class GameRoom {
	@KeyColumn
	private Long id;
	private String gameKey;
	private GameType gameType;
	private String hsk;
	private String lessons;
	private Level level;
	private Long hostId;
	private String createdDate;
	private String updatedDate;
	private String content;
	
	public GameRoom() {
	}
	public GameRoom(Long id, String gameKey, GameType gameType, String hsk, String lessons, Level level, Long hostId,
			String createdDate, String updatedDate, String content) {
		this.id = id;
		this.gameKey = gameKey;
		this.gameType = gameType;
		this.hsk = hsk;
		this.lessons = lessons;
		this.level = level;
		this.hostId = hostId;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.content = content;
	}
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
	public String getHsk() {
		return hsk;
	}
	public void setHsk(String hsk) {
		this.hsk = hsk;
	}
	public String getLessons() {
		return lessons;
	}
	public void setLessons(String lessons) {
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
