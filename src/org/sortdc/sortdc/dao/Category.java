package org.sortdc.sortdc.dao;

import java.util.List;

public class Category {

    private String id;
    private String name;

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

    public List<Category> findAll() {
        // TODO
        return null;
    }
}
