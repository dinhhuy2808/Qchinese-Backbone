package com.elearning.jerseyguice.model;

import java.util.List;

public class GetUserDictionaryResponse {
	private List<String> tabs;
	private UserDictionary userDictionary;
	private List<Dictionary> dictionaries;
	public List<Dictionary> getDictionaries() {
		return dictionaries;
	}
	public void setDictionaries(List<Dictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
	public List<String> getTabs() {
		return tabs;
	}
	public void setTabs(List<String> tabs) {
		this.tabs = tabs;
	}
	public UserDictionary getUserDictionary() {
		return userDictionary;
	}
	public void setUserDictionary(UserDictionary userDictionary) {
		this.userDictionary = userDictionary;
	}
}
