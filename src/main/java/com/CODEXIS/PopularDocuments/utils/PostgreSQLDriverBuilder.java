package com.CODEXIS.PopularDocuments.utils;
import com.CODEXIS.PopularDocuments.PopularDocumentsApplication;
import com.CODEXIS.PopularDocuments.controller.DocumentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import javax.print.Doc;
import java.sql.*;
import java.util.concurrent.locks.StampedLock;

public final class PostgreSQLDriverBuilder {
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;

    public PostgreSQLDriverBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public PostgreSQLDriverBuilder setPort(Integer port) {
        this.port = port;
        return this;
    }

    public PostgreSQLDriverBuilder setDatabase(String database) {
        this.database = database;
        return this;
    }

    public PostgreSQLDriverBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public PostgreSQLDriverBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public PostgreSQLDriver create() {
        return new PostgreSQLDriver(host, port, database, username, password);
    }

    public PostgreSQLDriver create(String host, Integer port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        return new PostgreSQLDriver(host, port, database, username, password);
    }

    public class PostgreSQLDriver {
        protected static Connection conn;
        protected String host;
        protected Integer port;
        protected String database;
        protected String username;
        protected String password;

        public PostgreSQLDriver(String host, Integer port, String database, String username, String password) {
            this.setup(host, port, database, username, password);
        }

        public PostgreSQLDriver changeConnection(String host, Integer port, String database, String username, String password){
            this.setup(host, port, database, username, password);
            return this;
        }

        public static void replaceConnection(Connection conn) {
            PostgreSQLDriver.conn = conn;
        }

        public PostgreSQLDriver changeDatabase(String database) {
            this.database = database;
            return this;
        }

        protected void setup(String host, Integer port, String database, String username, String password){
            this.host = host;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
        }

        public ResultSet executeStatement(String query) throws SQLException {
            Statement stmt = this.conn.createStatement();
            stmt.execute( query );
            return stmt.getResultSet();
        }

        public int executeUpdateStatement(String query) throws SQLException {
            return this.conn.createStatement().executeUpdate( query );
        }

        public PostgreSQLDriver connect() {
            try {
                Class.forName("org.postgresql.Driver");
                if(this.database.indexOf(0) != '/'){
                    this.database = '/' + this.database; // Maybe use string builder but like not really needed
                }
                this.conn = DriverManager.getConnection("jdbc:postgresql://" + this.host + ":" + this.port + this.database, this.username, this.password);
            } catch (Exception e) {
                PopularDocumentsApplication.logger.severe(e.getClass().getName()+": "+e.getMessage());
            }
            return this;
        }
    }
}