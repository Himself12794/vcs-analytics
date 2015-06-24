package com.cisco.dft.seed.api.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.cisco.dft.seed.api.util.Util;

/**
 * Defines attributes to connect to MongoDB database (with prefix "mongodb")
 * specified in the application.yml configuration file
 *
 * @author sujmuthu
 * @version 1.0
 * @date April 13, 2015
 */
@ConfigurationProperties(prefix = "mongodb")
public class MongoDBConfigParams {

    @Autowired
    private ApplicationConfigParams applicationConfigParams;

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private String authenticationDB;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthenticationDB() {
        return authenticationDB;
    }

    public void setAuthenticationDB(String authenticationDB) {
        this.authenticationDB = authenticationDB;
    }

    @PostConstruct
    public void decrypt() {
            this.password= Util.decrypt(this.password, applicationConfigParams.getDecryptionKey());
    }
}