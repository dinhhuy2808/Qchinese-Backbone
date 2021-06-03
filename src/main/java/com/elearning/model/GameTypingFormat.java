package com.elearning.model;

import java.util.List;

public class GameTypingFormat {
	private List<List<String>> words;
	
	public GameTypingFormat(List<List<String>> words) {
		this.words = words;
	}

	public List<List<String>> getWords() {
		return words;
	}
	public void setWords(List<List<String>> words) {
		this.words = words;
	}
}
