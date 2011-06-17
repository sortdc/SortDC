package org.sortdc.sortdc.dao;

import java.util.Map;

public class Document {

    private String id;
    private String category_id;
    private Map<String, Integer> words;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return this.category_id;
    }

    public void setCategoryId(String category_id) {
        this.category_id = category_id;
    }

    public Map<String, Integer> getWordsOccurrences() {
        return this.words;
    }

    public void setWordsOccurrences(Map<String, Integer> words) {
        this.words = words;
    }
}
