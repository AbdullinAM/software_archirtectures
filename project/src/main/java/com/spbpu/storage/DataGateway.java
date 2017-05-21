package com.spbpu.storage;

/**
 * Created by kivi on 08.05.17.
 */

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

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

    public void dropAll() throws SQLException {
        executeSqlScript(getDataSource().getConnection(), new File("/home/kivi/workspace/software_archirtectures/project/db/db_create.sql"));
    }

    private void executeSqlScript(Connection conn, File inputFile) {

        // Delimiter
        String delimiter = ";";

        // Create scanner
        Scanner scanner;
        try {
            scanner = new Scanner(inputFile).useDelimiter(delimiter);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }

        // Loop through the SQL file statements
        Statement currentStatement = null;
        while(scanner.hasNext()) {

            // Get statement
            String rawStatement = scanner.next() + delimiter;
            try {
                // Execute statement
                currentStatement = conn.createStatement();
                currentStatement.execute(rawStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Release resources
                if (currentStatement != null) {
                    try {
                        currentStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                currentStatement = null;
            }
        }
        scanner.close();
    }

}