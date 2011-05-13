package org.sortdc.sortdc.dao;

import java.util.Map;
import org.sortdc.sortdc.Config;

public class Document {

    private String id;
    private String name;
    private String category_id;
    private Map<String, Integer> words;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static Document findById(String id) throws Exception {
        return Config.getInstance().getDatabase().findDocumentById(id);
    }

    public static Document findByName(String name) throws Exception {
        return Config.getInstance().getDatabase().findDocumentByName(name);
    }

    public void save() throws Exception {
        Config.getInstance().getDatabase().saveDocument(this);
    }
}
