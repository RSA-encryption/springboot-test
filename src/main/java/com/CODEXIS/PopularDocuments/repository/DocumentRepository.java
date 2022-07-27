package com.CODEXIS.PopularDocuments.repository;

import com.CODEXIS.PopularDocuments.PopularDocumentsApplication;
import com.CODEXIS.PopularDocuments.model.Document;
import com.CODEXIS.PopularDocuments.utils.PostgreSQLDriverBuilder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Repository
public class DocumentRepository {
    private UserRepository userRepository;
    private PostgreSQLDriverBuilder.PostgreSQLDriver conn;

    public DocumentRepository() {
        this.conn = new PostgreSQLDriverBuilder()
                .create("localhost", 5432, "test", "postgres", "password")
                .connect();
        this.userRepository = new UserRepository();
    }

    public List<Document> getDocument(UUID uuid) {
        ArrayList<Document> results = new ArrayList<>();
        try {
            try(ResultSet rs = this.conn.executeStatement("SELECT * FROM codexis.documents WHERE uuid = '" + uuid + "';")){
                while ( rs.next() ) {
                    results.add(
                            new Document(UUID.fromString(rs.getString("uuid")),
                            userRepository.getUser(UUID.fromString(rs.getString("user_uuid"))).get(0),
                            rs.getTimestamp("timestamp"))
                    );
                }
            }
        } catch ( Exception e ) {
            PopularDocumentsApplication.logger.log(Level.SEVERE, e.getClass().getName()+": "+e.getMessage());
        }
        return results;
    }
}
