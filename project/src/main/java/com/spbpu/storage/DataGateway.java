package com.spbpu.storage;

/**
 * Created by kivi on 08.05.17.
 */

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.IOException;

/**
 * Created by Admin on 21.04.2016.
 */
public class DataGateway {

    static final String url = "jdbc:mysql://localhost/pmsdb";
    static final String user = "user";
    static final String password = "1";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static DataGateway dataGateway;
    private static MysqlDataSource dataSource;

    private DataGateway() throws IOException {
        //Структура соединения с базой данных
        dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException se) {
            se.printStackTrace();
        }
    }

    public static DataGateway getInstance() throws IOException {
        if(dataGateway == null)
            dataGateway = new DataGateway();
        return dataGateway;
    }

    public MysqlDataSource getDataSource() {
        return dataSource;
    }

}