package org.sortdc.sortdc.dao;

import java.util.Map;

public class Document {

    private String id;
    private String category_id;
    private Map<String, Integer> tokens;

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

    public Map<String, Integer> getTokensOccurrences() {
        return this.tokens;
    }

    public void setTokensOccurrences(Map<String, Integer> tokens) {
        this.tokens = tokens;
    }
}
