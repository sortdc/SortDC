package org.sortdc.sortdc.dao.interfaces;

import java.util.Map;

public interface DocumentInterface {

    public String getId();

    public void setId(String id);

    public String getName();

    public void setName(String name);

    public String getCategoryId();

    public void setCategoryId(String category_id);

    public Map<String, Integer> getWordsOccurrences();

    public void setWordsOccurrences(Map<String, Integer> words);
}
