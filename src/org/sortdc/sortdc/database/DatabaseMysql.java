package org.sortdc.sortdc.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseMysql extends Database {

    private static Database instance;
    private Connection db;

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

    public void connect() throws Exception {
        this.db = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.db_name, this.username, this.password);
    }
}
