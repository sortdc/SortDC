package org.sortdc.sortdc.dao;

import java.util.Map;

public class Word {

    private String id;
    private String name;
    private Map<String, Integer> occurences_by_cat;
    private Integer occurences = null;

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
        return this.occurences_by_cat;
    }

    public void setOccurrencesByCategory(Map<String, Integer> occurences) {
        this.occurences_by_cat = occurences;
    }

    public int getOccurrences() {
        if (this.occurences == null) {
            this.occurences = 0;
            for (int n : this.occurences_by_cat.values()) {
                this.occurences += n;
            }
        }
        return this.occurences;
    }
}
