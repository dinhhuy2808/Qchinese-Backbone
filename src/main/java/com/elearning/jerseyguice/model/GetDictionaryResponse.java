package com.elearning.jerseyguice.model;

import java.util.List;

public class GetDictionaryResponse {
	private List<Dictionary> dictionaries;
	private int count;
	public List<Dictionary> getDictionaries() {
		return dictionaries;
	}
	public void setDictionaries(List<Dictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
