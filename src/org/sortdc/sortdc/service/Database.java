package org.sortdc.sortdc.service;

public abstract class Database {
    protected String host;
    protected int port;
    protected String db_name;
    protected String username;
    protected String password;

    public void setHost(String host){
        this.host = host;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setDbName(String db_name){
        this.db_name = db_name;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
