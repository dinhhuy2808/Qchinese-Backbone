package com.elearning.jerseyguice.model.Request;

import java.util.List;

public class UserDictionarySearchParam {
    private List<Integer> createDate;
    private List<Integer> hsk;
    private List<String> lesson;
    private String hantu;

    public UserDictionarySearchParam() {
    }

    public UserDictionarySearchParam(List<Integer> createDate, List<Integer> hsk, List<String> lesson, String hantu) {
        this.createDate = createDate;
        this.hsk = hsk;
        this.lesson = lesson;
        this.hantu = hantu;
    }

    public List<Integer> getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(List<Integer> createDate) {
        this.createDate = createDate;
    }

    public List<Integer> getHsk() {
        return this.hsk;
    }

    public void setHsk(List<Integer> hsk) {
        this.hsk = hsk;
    }

    public List<String> getLesson() {
        return this.lesson;
    }

    public void setLesson(List<String> lesson) {
        this.lesson = lesson;
    }

    public String getHantu() {
        return this.hantu;
    }

    public void setHantu(String hantu) {
        this.hantu = hantu;
    }

    public UserDictionarySearchParam createDate(List<Integer> createDate) {
        this.createDate = createDate;
        return this;
    }

    public UserDictionarySearchParam hsk(List<Integer> hsk) {
        this.hsk = hsk;
        return this;
    }

    public UserDictionarySearchParam lesson(List<String> lesson) {
        this.lesson = lesson;
        return this;
    }

    public UserDictionarySearchParam hantu(String hantu) {
        this.hantu = hantu;
        return this;
    }
}
