package org.sortdc.sortdc.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.sortdc.sortdc.dao.Category;

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
        List<Category> categories = new ArrayList();
        Statement statement = this.connection.createStatement();
        ResultSet data = statement.executeQuery("SELECT * FROM categories");
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
    public void saveCategory(Category category) throws Exception {
        if (category.getId() != null) {
            PreparedStatement statement = this.connection.prepareStatement("INSERT INTO categories (id, name) VALUES(?,?)");
            statement.setString(1, category.getId());
            statement.setString(2, category.getName());
            statement.execute();
        } else {
            PreparedStatement statement = this.connection.prepareStatement("UPDATE categories SET name = ? WHERE id = ?");
            statement.setString(1, category.getName());
            statement.setString(2, category.getId());
            statement.execute();
            if (statement.getUpdateCount() == 0) {
                category.setId(null);
                this.saveCategory(category);
            }
        }
    }
}
