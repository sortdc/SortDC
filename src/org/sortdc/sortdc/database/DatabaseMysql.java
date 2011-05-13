package org.sortdc.sortdc.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.dao.Word;

public class DatabaseMysql extends Database {

    private static Database instance;
    private Connection connection;

    private DatabaseMysql() {
    }

    /**
     * Creates a unique instance of DatabaseMysql (Singleton)
     *
     * @return Instance of DatabaseMysql
     */
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new DatabaseMysql();
        }
        return instance;
    }

    /**
     * Establishes connection with database
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.db_name, this.username, this.password);
    }

    /**
     * Finds all registered categories
     *
     * @return Categories list
     * @throws Exception
     */
    public List<Category> findAllCategories() throws Exception {
        List<Category> categories = new ArrayList<Category>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT id, name FROM categories");
        ResultSet data = statement.executeQuery();
        while (data.next()) {
            Category category = new Category();
            category.setId(data.getString("id"));
            category.setName(data.getString("name"));
            categories.add(category);
        }
        return categories;
    }

    /**
     * Saves or updates a category
     *
     * @param category
     * @throws Exception
     */
    public synchronized void saveCategory(Category category) throws Exception {
        if (category.getId() == null) {
            PreparedStatement statement = this.connection.prepareStatement("INSERT INTO categories (name) VALUES (?)");
            statement.setString(2, category.getName());
            statement.execute();
        } else {
            PreparedStatement statement = this.connection.prepareStatement("UPDATE categories SET name = ? WHERE id = ?");
            statement.setString(1, category.getName());
            statement.setInt(2, Integer.parseInt(category.getId()));
            statement.execute();
            if (statement.getUpdateCount() == 0) {
                throw new Exception("Category not found");
            }
        }
    }

    public Document findDocumentById(String id) throws Exception {
        // TODO
        return null;
    }

    public Document findDocumentByName(String name) throws Exception {
        // TODO
        return null;
    }

    public synchronized void saveDocument(Document document) throws Exception {
        // TODO
    }

    public Word findWordById(String id) throws Exception {
        // TODO
        return null;
    }

    public Word findWordByName(String name) throws Exception {
        // TODO
        return null;
    }

    public List<Word> findWordByNames(Set<String> names) throws Exception {
        // TODO
        return null;
    }

    public synchronized void saveWord(Word word) throws Exception {
        // TODO
    }
}
