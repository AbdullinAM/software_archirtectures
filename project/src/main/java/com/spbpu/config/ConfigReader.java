package com.spbpu.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by kivi on 29.05.17.
 */
public class ConfigReader {

    InputStream inputStream;
    private String dburl;
    private String dbuser;
    private String dbpassword;
    private String emailAccount;
    private String emailPassword;
    private static ConfigReader instance = null;

    public static ConfigReader getInstance() throws IOException {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    private ConfigReader() throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            dburl = prop.getProperty("dburl");
            dbuser = prop.getProperty("dbuser");
            dbpassword = prop.getProperty("dbpassword");
            emailAccount = prop.getProperty("emailAccount");
            emailPassword = prop.getProperty("emailPassword");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }

    public String getDburl() {
        return dburl;
    }

    public String getDbuser() {
        return dbuser;
    }

    public String getDbpassword() {
        return dbpassword;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public String getEmailPassword() {
        return emailPassword;
    }
}

