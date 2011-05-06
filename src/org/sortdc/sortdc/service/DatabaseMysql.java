package org.sortdc.sortdc.service;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseMysql extends Database {
    private Connection db;
    
    public void connect() throws Exception {
        this.db = DriverManager.getConnection("jdbc:mysql://"+this.host+"/"+this.db_name, this.username, this.password);   
    }
}
