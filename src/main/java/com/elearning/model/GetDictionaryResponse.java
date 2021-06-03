package com.elearning.model;

import java.util.List;

import com.elearning.entity.Dictionary;

public class GetDictionaryResponse {
	private List<Dictionary> dictionaries;
	private Long count;
	public List<Dictionary> getDictionaries() {
		return dictionaries;
	}
	public void setDictionaries(List<Dictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	
}
