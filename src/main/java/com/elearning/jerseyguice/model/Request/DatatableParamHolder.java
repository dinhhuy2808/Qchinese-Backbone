package com.elearning.jerseyguice.model.Request;

public class DatatableParamHolder {
    private DatatableParam datatableParam;
    private UserDictionarySearchParam userDictionarySearchParam;


    public DatatableParamHolder() {
    }

    public DatatableParamHolder(DatatableParam datatableParam, UserDictionarySearchParam userDictionarySearchParam) {
        this.datatableParam = datatableParam;
        this.userDictionarySearchParam = userDictionarySearchParam;
    }

    public DatatableParam getDatatableParam() {
        return this.datatableParam;
    }

    public void setDatatableParam(DatatableParam datatableParam) {
        this.datatableParam = datatableParam;
    }

    public UserDictionarySearchParam getUserDictionarySearchParam() {
        return this.userDictionarySearchParam;
    }

    public void setUserDictionarySearchParam(UserDictionarySearchParam userDictionarySearchParam) {
        this.userDictionarySearchParam = userDictionarySearchParam;
    }

    public DatatableParamHolder datatableParam(DatatableParam datatableParam) {
        this.datatableParam = datatableParam;
        return this;
    }

    public DatatableParamHolder userDictionarySearchParam(UserDictionarySearchParam userDictionarySearchParam) {
        this.userDictionarySearchParam = userDictionarySearchParam;
        return this;
    }
}
