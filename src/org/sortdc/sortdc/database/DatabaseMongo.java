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
}
