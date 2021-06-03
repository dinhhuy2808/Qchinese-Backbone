package com.elearning.model;

import java.util.List;
import java.util.Map;

public class GameMatchFormat {
	private List<List<String>> words;
	private Map<String, String> result;
	private Map<String, String> reverseResult;
	
	public GameMatchFormat(List<List<String>> words, Map<String, String> result, Map<String, String> reverseResult) {
		this.words = words;
		this.result = result;
		this.reverseResult = reverseResult;
	}

	public List<List<String>> getWords() {
		return words;
	}

	public void setWords(List<List<String>> words) {
		this.words = words;
	}

	public Map<String, String> getResult() {
		return result;
	}

	public void setResult(Map<String, String> result) {
		this.result = result;
	}

	public Map<String, String> getReverseResult() {
		return reverseResult;
	}

	public void setReverseResult(Map<String, String> reverseResult) {
		this.reverseResult = reverseResult;
	}
	
}
