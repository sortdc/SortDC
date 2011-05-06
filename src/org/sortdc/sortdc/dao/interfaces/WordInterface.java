package org.sortdc.sortdc.dao.interfaces;

import java.util.Map;

public interface WordInterface {

    public String getId();

    public void setId(String id);

    public String getName();

    public void setName(String name);

    public Map<String, Integer> getOccurrencesByCategory();

    public void setOccurrencesByCategory(Map<String, Integer> occurences);
}
