package org.sortdc.sortdc.dao;

import java.util.List;
import org.sortdc.sortdc.Config;

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

    public List<Category> findAll() throws Exception {
        return Config.getInstance().getDatabase().findAllCategories();
    }

    public void save() throws Exception {
        Config.getInstance().getDatabase().saveCategory(this);
    }
}
