package com.elearning.dto;

import java.util.List;

public class StringListDTO {
    private List<String> values;

    public StringListDTO() {
    }

    public StringListDTO(List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public StringListDTO values(List<String> values) {
        this.values = values;
        return this;
    }
}
