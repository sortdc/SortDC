package org.sortdc.sortdc.database.interfaces;

import java.util.List;
import java.util.Set;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.dao.Token;

public interface DatabaseInterface {

    public void connect() throws Exception;

    public void setHost(String host);

    public void setPort(int port);

    public void setDbName(String db_name);

    public void setUsername(String username);

    public void setPassword(String password);

    public List<Category> findAllCategories() throws Exception;

    public void saveCategory(Category category) throws Exception;

    public void deleteCategoryById(String category_id) throws Exception;

    public Document findDocumentById(String document_id) throws Exception;

    public void saveDocument(Document document) throws Exception;

    public void deleteDocumentById(String document_id) throws Exception;

    public Token findTokenById(String id) throws Exception;

    public Token findTokenByName(String name) throws Exception;

    public List<Token> findTokensByNames(Set<String> names) throws Exception;
}
