package org.sortdc.sortdc.database.interfaces;

import java.util.List;
import org.sortdc.sortdc.dao.Category;

public interface DatabaseInterface {

    public void connect() throws Exception;

    public void setHost(String host);

    public void setPort(int port);

    public void setDbName(String db_name);

    public void setUsername(String username);

    public void setPassword(String password);

    public List<Category> findAllCategories() throws Exception;

    public void saveCategory(Category category) throws Exception;
}
