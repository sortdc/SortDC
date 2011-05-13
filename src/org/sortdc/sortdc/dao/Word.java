package org.sortdc.sortdc.dao;

import java.util.Map;
import org.sortdc.sortdc.Config;

public class Word {

    private String id;
    private String name;
    private Map<String, Integer> occurences;

    private Word() {
    }

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

    public Map<String, Integer> getOccurrencesByCategory() {
        return this.occurences;
    }

    public void setOccurrencesByCategory(Map<String, Integer> occurences) {
        this.occurences = occurences;
    }

    public static Word findById(String id) throws Exception {
        return Config.getInstance().getDatabase().findWordById(id);
    }

    public static Word findByName(String name) throws Exception {
        return Config.getInstance().getDatabase().findWordByName(name);
    }
}
