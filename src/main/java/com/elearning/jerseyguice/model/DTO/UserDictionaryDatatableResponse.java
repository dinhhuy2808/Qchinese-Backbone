package com.elearning.jerseyguice.model.DTO;

import java.util.List;

public class UserDictionaryDatatableResponse {
    private int draw;
    private int rescordsTotal;
    private int recordsFiltered;
    private List<UserDictionaryDTO> data;
    private String error;

    public UserDictionaryDatatableResponse() {
    }

    public UserDictionaryDatatableResponse(int draw, int rescordsTotal, int recordsFiltered, List<UserDictionaryDTO> data, String error) {
        this.draw = draw;
        this.rescordsTotal = rescordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
        this.error = error;
    }

    public int getDraw() {
        return this.draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getRescordsTotal() {
        return this.rescordsTotal;
    }

    public void setRescordsTotal(int rescordsTotal) {
        this.rescordsTotal = rescordsTotal;
    }

    public int getRecordsFiltered() {
        return this.recordsFiltered;
    }

    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<UserDictionaryDTO> getData() {
        return this.data;
    }

    public void setData(List<UserDictionaryDTO> data) {
        this.data = data;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public UserDictionaryDatatableResponse draw(int draw) {
        this.draw = draw;
        return this;
    }

    public UserDictionaryDatatableResponse rescordsTotal(int rescordsTotal) {
        this.rescordsTotal = rescordsTotal;
        return this;
    }

    public UserDictionaryDatatableResponse recordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
        return this;
    }

    public UserDictionaryDatatableResponse data(List<UserDictionaryDTO> data) {
        this.data = data;
        return this;
    }

    public UserDictionaryDatatableResponse error(String error) {
        this.error = error;
        return this;
    }
}
