package org.sortdc.sortdc.database;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import java.util.ArrayList;
import java.util.List;
import org.sortdc.sortdc.dao.Category;

public class DatabaseMongo extends Database {

    private static Database instance;
    private DB db;

    private DatabaseMongo() {
    }

    /**
     * Creates a unique instance of DatabaseMongo (Singleton)
     *
     * @return Instance of DatabaseMongo
     */
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new DatabaseMongo();
        }
        return instance;
    }

    /**
     * Establishes connection with database
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        Mongo init = new Mongo(this.host, this.port);

        DB database = init.getDB(this.db_name);
        if (this.username != null && this.password != null && !db.authenticate(this.username, this.password.toCharArray())) {
            throw new Exception("Connection denied : incorrect database authentication");
        }

        this.db = database;
    }

    /**
     * Finds all registered categories
     * 
     * @return Categories list
     * @throws Exception
     */
    public List<Category> findAllCategories() throws Exception {
        List<Category> categories = new ArrayList();
        DBCollection collection = this.db.getCollection("categories");
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Category category = new Category();
            category.setId((String) obj.get("_id"));
            category.setName((String) obj.get("name"));
            categories.add(category);
        }
        return categories;
    }
}
