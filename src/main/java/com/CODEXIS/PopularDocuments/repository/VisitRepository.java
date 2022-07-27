package com.CODEXIS.PopularDocuments.repository;

import com.CODEXIS.PopularDocuments.PopularDocumentsApplication;
import com.CODEXIS.PopularDocuments.model.Score;
import com.CODEXIS.PopularDocuments.model.ScoreEntry;
import com.CODEXIS.PopularDocuments.utils.PostgreSQLDriverBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

@Repository
public class VisitRepository {
    private PostgreSQLDriverBuilder.PostgreSQLDriver conn;

    public VisitRepository() {
        // Postgre details should be in config
        this.conn = new PostgreSQLDriverBuilder()
                .create("localhost", 5432, "test", "postgres", "password")
                .connect();
    }

    public ResponseEntity<String> visit(UUID docUUID) {
        try{
            if (this.conn.executeUpdateStatement("INSERT INTO codexis.visits(doc_uuid) VALUES (UUID('" + docUUID.toString() + "'))") == 1) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            PopularDocumentsApplication.logger.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ArrayList<Score> getScore(Integer limit, Boolean useRelativeVariance, String from, String to) {
        ArrayList<ScoreEntry> results = new ArrayList<>();
        ArrayList<Float> floatArrayList = new ArrayList<>();
        Float variance = 0.0f;
        try {
            // Z-Score Calculation

            /***
             * Okay hear me out. First time using Postgres. I know this could be optimized so that the first fetched table gets reused again.
             * Resp rewriting it into a function (Maybe stored procedure ?).
             * Another thing I know I should be using prepared statements, but I've spent way too much time on this estimate, and I don't have the energy right now.
             * I am sleep-deprived, and I just want to sleep lol.
             * (Another thing I am not SQL god, and I rarely had to do complex things with sql. Normally I would ask someone more experienced but like… not this time)
             */

            /***
             * Whole idea behind this whole calculation. Every document will have its own Z-Score so that we can easily track which documents are the most popular.
             * Using the variance we can easily get the top documents' resp if we want like the hottest most viewed documents we should look for documents that have
             * z-index value > (2.5 * variance). If we want common documents and above just use variance. If you want documents that are popular and are pretty much not
             * the hottest picks search for variance between 1.5-2, and it should yield pretty good results.
             */

            /**
             * Last note. Weekly, daily and hourly popular docs could be easily cached, so we can speed this up.
             * Cron tasks would be perfect for this (or it could be recalculated every hour or so)
             */
            try(ResultSet rs = this.conn.executeStatement(
                    "SELECT ABS(COUNT(codexis.Visits.doc_uuid) -\n" +
                            "             (SELECT reltuples::bigint AS COUNT\n" +
                            "              FROM pg_class\n" +
                            "              WHERE oid = to_regclass('codexis.visits')) /\n" +
                            "             (SELECT reltuples::bigint AS COUNT\n" +
                            "              FROM pg_class\n" +
                            "              WHERE oid = to_regclass('codexis.documents'))::float) /\n" +
                            "  \t(SELECT STDDEV_SAMP(standard_deviation)\n" +
                            "   \tFROM\n" +
                            "     \t\t(SELECT COUNT(codexis.Visits.doc_uuid) AS standard_deviation\n" +
                            "      \tFROM codexis.Visits\n" +
                            "      \tINNER JOIN codexis.Documents ON codexis.Documents.uuid = codexis.Visits.doc_uuid\n" +
                            "      \tGROUP BY codexis.Visits.doc_uuid) AS sd) AS z_score,\n" +
                            "       \tcodexis.Visits.doc_uuid\n" +
                            "\tFROM codexis.Visits\n" +
                            "\tWHERE " +
                            (from != null && to != null ? "codexis.Visits.timestamp::timestamp >= '" + from + "'::timestamp AND codexis.Visits.timestamp::timestamp <= '" + to + "'::timestamp AND " : "") +
                            "(EXISTS\n" +
                            "         (SELECT 1\n" +
                            "          FROM codexis.Documents\n" +
                            "          WHERE codexis.Documents.uuid = codexis.Visits.doc_uuid ))\n" +
                            "\tGROUP BY codexis.Visits.doc_uuid\n" +
                            "\tORDER BY z_score DESC\n" +
                            "\tLIMIT " + limit +";"))
            {
                while (rs.next()) {
                    if(useRelativeVariance) {
                        floatArrayList.add(rs.getFloat("z_score"));
                    }
                    results.add(new ScoreEntry(UUID.fromString(rs.getString("doc_uuid")), rs.getFloat("z_score")));
                }
            }
            if(!useRelativeVariance){
                try(ResultSet rs = this.conn.executeStatement("SELECT STDDEV(z_score)\n" +
                        "   \tFROM\n" +
                        "(SELECT ABS(COUNT(codexis.Visits.doc_uuid) -\n" +
                        "             (SELECT reltuples::bigint AS COUNT\n" +
                        "              FROM pg_class\n" +
                        "              WHERE oid = to_regclass('codexis.visits')) /\n" +
                        "             (SELECT reltuples::bigint AS COUNT\n" +
                        "              FROM pg_class\n" +
                        "              WHERE oid = to_regclass('codexis.documents'))::float) /\n" +
                        "  \t(SELECT STDDEV_SAMP(standard_deviation)\n" +
                        "   \tFROM\n" +
                        "     \t\t(SELECT COUNT(codexis.Visits.doc_uuid) AS standard_deviation\n" +
                        "      \tFROM codexis.Visits\n" +
                        "      \tINNER JOIN codexis.Documents ON codexis.Documents.uuid = codexis.Visits.doc_uuid\n" +
                        "      \tGROUP BY codexis.Visits.doc_uuid) AS sd) AS z_score,\n" +
                        "       \tcodexis.Visits.doc_uuid\n" +
                        "\tFROM codexis.Visits\n" +
                        "\tWHERE (EXISTS\n" +
                        "         (SELECT 1\n" +
                        "          FROM codexis.Documents\n" +
                        "          WHERE codexis.Documents.uuid = codexis.Visits.doc_uuid ))\n" +
                        "\tGROUP BY codexis.Visits.doc_uuid\n" +
                        "\tORDER BY z_score DESC) as q;\n"
                ))
                {
                    rs.next();
                    variance = rs.getFloat("stddev");
                }
            } else {
                /**
                 * In case we just want to calculate variance from retrieved rows and not from the entire dataset.
                 */
                double mean = 0.0;
                for (int i = 0; i < floatArrayList.size(); i++) {
                    mean += floatArrayList.get(i);
                }
                mean /= floatArrayList.size();

                for (int i = 0; i < floatArrayList.size(); i++) {
                    variance += (float) Math.pow(floatArrayList.get(i) - mean, 2);
                }
                variance /= floatArrayList.size();
            }
        } catch (Exception e) {
            PopularDocumentsApplication.logger.log(Level.SEVERE, e.getClass().getName() + ": " + e.getMessage());
        }
        Float what = variance; // What..
        return new ArrayList<>() {
            {
                add(new Score(results, what));
            }
        };
    }
}
