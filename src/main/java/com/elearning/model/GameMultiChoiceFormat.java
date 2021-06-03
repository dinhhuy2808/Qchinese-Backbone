package com.elearning.model;

import java.util.List;

public class GameMultiChoiceFormat {
	private String soundName;
	private List<String> choices;
	private String result;
	
	public GameMultiChoiceFormat(String soundName, List<String> choices, String result) {
		this.soundName = soundName;
		this.choices = choices;
		this.result = result;
	}
	public String getSoundName() {
		return soundName;
	}
	public void setSoundName(String soundName) {
		this.soundName = soundName;
	}
	public List<String> getChoices() {
		return choices;
	}
	public void setChoices(List<String> choices) {
		this.choices = choices;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
