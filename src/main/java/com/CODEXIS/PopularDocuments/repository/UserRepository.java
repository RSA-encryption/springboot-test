package com.CODEXIS.PopularDocuments.repository;

import com.CODEXIS.PopularDocuments.PopularDocumentsApplication;
import com.CODEXIS.PopularDocuments.model.User;
import com.CODEXIS.PopularDocuments.utils.PostgreSQLDriverBuilder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Repository
public class UserRepository {
    private PostgreSQLDriverBuilder.PostgreSQLDriver conn;

    public UserRepository() {
        this.conn = new PostgreSQLDriverBuilder()
                .create("localhost", 5432, "test", "postgres", "password")
                .connect();
    }

    public List<User> getUser(UUID uuid) {
        ArrayList<User> results = new ArrayList<>();
        try {
            try(ResultSet rs = this.conn.executeStatement("SELECT * FROM codexis.users WHERE uuid = '" + uuid.toString() + "';")){
                while ( rs.next() ) {
                    results.add(new User(UUID.fromString(rs.getString("uuid")), rs.getString("name"), rs.getTimestamp("timestamp")));
                }
            }
        } catch ( Exception e ) {
            PopularDocumentsApplication.logger.log(Level.SEVERE, e.getClass().getName()+": "+e.getMessage());
        }
        return results;
    }
}
