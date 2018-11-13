package com.elasticsearch.xcontent;

public class SContentBuilder {

    private String keyName;
    private SContentBuilderType keyType;

    private SContentBuilderAnalyzer analyzerType;

    public SContentBuilder() {

    }

    public SContentBuilder(String keyName, SContentBuilderType keyType) {
        this.keyName = keyName;
        this.keyType = keyType;
        this.analyzerType = SContentBuilderAnalyzer.close;
    }

    public SContentBuilder(String keyName, SContentBuilderType keyType, SContentBuilderAnalyzer analyzerType) {
        this.keyName = keyName;
        this.keyType = keyType;
        this.analyzerType = analyzerType;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyType() {
        switch (keyType) {
            case string:
                return "text";
            case integer:
                return "integer";
            default:
                return null;
        }
    }

    public void setKeyType(SContentBuilderType keyType) {
        this.keyType = keyType;
    }

    public String getAnalyzerType() {
        switch (analyzerType) {
            case ik_smart:
                return "ik_smart";
            case ik_max_word:
                return "ik_max_word";
            case close:
            default:
                return null;
        }
    }

    public void setAnalyzerType(SContentBuilderAnalyzer analyzerType) {
        this.analyzerType = analyzerType;
    }

}
