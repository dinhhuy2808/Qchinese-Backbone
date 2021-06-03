package com.elearning.model;

import com.elearning.constant.Level;

public class GameInfo {
	private String gameKey;
	private Level level;
	private GameContent content;
	public GameInfo(String gameKey, Level level, GameContent content) {
		super();
		this.gameKey = gameKey;
		this.level = level;
		this.content = content;
	}
	
}
