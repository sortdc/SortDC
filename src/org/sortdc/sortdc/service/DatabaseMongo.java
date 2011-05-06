package org.sortdc.sortdc.service;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class DatabaseMongo extends Database {
    private DB db;

    public void connect() throws Exception {
        Mongo init = new Mongo(this.host, this.port);
        this.db = init.getDB(this.db_name);
    }
}
