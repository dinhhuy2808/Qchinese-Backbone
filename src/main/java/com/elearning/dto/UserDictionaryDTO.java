package com.elearning.dto;

import java.time.LocalDateTime;

public class UserDictionaryDTO {
	private Long wordId;
	private String hantu;
	private String pinyin;
	private String nghia1;
	private String hanviet;
	private Integer hsk;
	private String lesson;
	private String part;
	private Integer standart;
	private Integer popular;
	private LocalDateTime createDate;
	private String tab;
	private String place;
	private boolean isChecked;
	private String input;
	private Integer correctLevel;
	private boolean isHantuHidden;
	private boolean isPinyinHidden;
	private boolean isNghiaHidden;
	private boolean isAudioHidden;

	public String getWordIdAsString() {
        return Long.toString(this.wordId);
	}
	
	public String getKey() {
        return this.hsk+"-"+this.lesson;
    }

	public UserDictionaryDTO() {
	}

	public UserDictionaryDTO(Long wordId, String hantu, String pinyin, String nghia1, String hanviet, Integer hsk, String lesson, String part, Integer standart, Integer popular, LocalDateTime createDate, String tab, String place, boolean isChecked, String input, Integer correctLevel, boolean isHantuHidden, boolean isPinyinHidden, boolean isNghiaHidden, boolean isAudioHidden) {
		this.wordId = wordId;
		this.hantu = hantu;
		this.pinyin = pinyin;
		this.nghia1 = nghia1;
		this.hanviet = hanviet;
		this.hsk = hsk;
		this.lesson = lesson;
		this.part = part;
		this.standart = standart;
		this.popular = popular;
		this.createDate = createDate;
		this.tab = tab;
		this.place = place;
		this.isChecked = isChecked;
		this.input = input;
		this.correctLevel = correctLevel;
		this.isHantuHidden = isHantuHidden;
		this.isPinyinHidden = isPinyinHidden;
		this.isNghiaHidden = isNghiaHidden;
		this.isAudioHidden = isAudioHidden;
	}

	public Long getWordId() {
		return this.wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public String getHantu() {
		return this.hantu;
	}

	public void setHantu(String hantu) {
		this.hantu = hantu;
	}

	public String getPinyin() {
		return this.pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getNghia1() {
		return this.nghia1;
	}

	public void setNghia1(String nghia1) {
		this.nghia1 = nghia1;
	}

	public String getHanviet() {
		return this.hanviet;
	}

	public void setHanviet(String hanviet) {
		this.hanviet = hanviet;
	}

	public Integer getHsk() {
		return this.hsk;
	}

	public void setHsk(Integer hsk) {
		this.hsk = hsk;
	}

	public String getLesson() {
		return this.lesson;
	}

	public void setLesson(String lesson) {
		this.lesson = lesson;
	}

	public String getPart() {
		return this.part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public Integer getStandart() {
		return this.standart;
	}

	public void setStandart(Integer standart) {
		this.standart = standart;
	}

	public Integer getPopular() {
		return this.popular;
	}

	public void setPopular(Integer popular) {
		this.popular = popular;
	}

	public LocalDateTime getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public String getTab() {
		return this.tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getPlace() {
		return this.place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public boolean isIsChecked() {
		return this.isChecked;
	}

	public boolean getIsChecked() {
		return this.isChecked;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getInput() {
		return this.input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public Integer getCorrectLevel() {
		return this.correctLevel;
	}

	public void setCorrectLevel(Integer correctLevel) {
		this.correctLevel = correctLevel;
	}

	public boolean isIsHantuHidden() {
		return this.isHantuHidden;
	}

	public boolean getIsHantuHidden() {
		return this.isHantuHidden;
	}

	public void setIsHantuHidden(boolean isHantuHidden) {
		this.isHantuHidden = isHantuHidden;
	}

	public boolean isIsPinyinHidden() {
		return this.isPinyinHidden;
	}

	public boolean getIsPinyinHidden() {
		return this.isPinyinHidden;
	}

	public void setIsPinyinHidden(boolean isPinyinHidden) {
		this.isPinyinHidden = isPinyinHidden;
	}

	public boolean isIsNghiaHidden() {
		return this.isNghiaHidden;
	}

	public boolean getIsNghiaHidden() {
		return this.isNghiaHidden;
	}

	public void setIsNghiaHidden(boolean isNghiaHidden) {
		this.isNghiaHidden = isNghiaHidden;
	}

	public boolean isIsAudioHidden() {
		return this.isAudioHidden;
	}

	public boolean getIsAudioHidden() {
		return this.isAudioHidden;
	}

	public void setIsAudioHidden(boolean isAudioHidden) {
		this.isAudioHidden = isAudioHidden;
	}

	public UserDictionaryDTO wordId(Long wordId) {
		this.wordId = wordId;
		return this;
	}

	public UserDictionaryDTO hantu(String hantu) {
		this.hantu = hantu;
		return this;
	}

	public UserDictionaryDTO pinyin(String pinyin) {
		this.pinyin = pinyin;
		return this;
	}

	public UserDictionaryDTO nghia1(String nghia1) {
		this.nghia1 = nghia1;
		return this;
	}

	public UserDictionaryDTO hanviet(String hanviet) {
		this.hanviet = hanviet;
		return this;
	}

	public UserDictionaryDTO hsk(Integer hsk) {
		this.hsk = hsk;
		return this;
	}

	public UserDictionaryDTO lesson(String lesson) {
		this.lesson = lesson;
		return this;
	}

	public UserDictionaryDTO part(String part) {
		this.part = part;
		return this;
	}

	public UserDictionaryDTO standart(Integer standart) {
		this.standart = standart;
		return this;
	}

	public UserDictionaryDTO popular(Integer popular) {
		this.popular = popular;
		return this;
	}

	public UserDictionaryDTO createDate(LocalDateTime createDate) {
		this.createDate = createDate;
		return this;
	}

	public UserDictionaryDTO tab(String tab) {
		this.tab = tab;
		return this;
	}

	public UserDictionaryDTO place(String place) {
		this.place = place;
		return this;
	}

	public UserDictionaryDTO isChecked(boolean isChecked) {
		this.isChecked = isChecked;
		return this;
	}

	public UserDictionaryDTO input(String input) {
		this.input = input;
		return this;
	}

	public UserDictionaryDTO correctLevel(Integer correctLevel) {
		this.correctLevel = correctLevel;
		return this;
	}

	public UserDictionaryDTO isHantuHidden(boolean isHantuHidden) {
		this.isHantuHidden = isHantuHidden;
		return this;
	}

	public UserDictionaryDTO isPinyinHidden(boolean isPinyinHidden) {
		this.isPinyinHidden = isPinyinHidden;
		return this;
	}

	public UserDictionaryDTO isNghiaHidden(boolean isNghiaHidden) {
		this.isNghiaHidden = isNghiaHidden;
		return this;
	}

	public UserDictionaryDTO isAudioHidden(boolean isAudioHidden) {
		this.isAudioHidden = isAudioHidden;
		return this;
	}
}
