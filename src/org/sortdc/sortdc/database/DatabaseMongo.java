package org.sortdc.sortdc.database;

import com.mongodb.DB;
import com.mongodb.Mongo;

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
     * Establishes the connextion to the database
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        Mongo init = new Mongo(this.host, this.port);
        this.db = init.getDB(this.db_name);
    }
}
