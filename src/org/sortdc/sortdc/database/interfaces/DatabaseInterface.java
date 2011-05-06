package org.sortdc.sortdc.database.interfaces;

public interface DatabaseInterface {

    public void connect() throws Exception;

    public void setHost(String host);

    public void setPort(int port);

    public void setDbName(String db_name);

    public void setUsername(String username);

    public void setPassword(String password);
}
