package com.CODEXIS.PopularDocuments.Scenarios;

import Core.BaseRestScenario;
import com.CODEXIS.PopularDocuments.model.Score;
import com.CODEXIS.PopularDocuments.model.ScoreEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class RatingScenario extends BaseRestScenario {
    @Test
    public void ratingScenario() throws Exception {
        this.performQuery(Files.readString(Paths.get(".//src//queries//generation.sql")));

        Score original = this.visitRepository.getScore(20, false, null, null).get(0);
        String originalTopDocumentUUID = original.entries().get(0).uuid().toString();
        Float oldRating = original.entries().get(0).score(), newRating = null;

        for(int i = 0; i < 500; ++i){
            performQuery("INSERT INTO codexis.visits(doc_uuid) VALUES (UUID('" + original.entries().get(original.entries().size() - 1).uuid().toString() + "'))");
        }
        /**
         * The idea behind this test is essentially get some not so popular item and raise its visits.
         * If everything is working even one visit should modify z-scores of pretty much anything. Running it 500 times cuz why not.
         */
        Score updated = this.visitRepository.getScore(20, false, null, null).get(0);
        for (ScoreEntry entry: updated.entries()) {
            if(entry.uuid().toString().equals(originalTopDocumentUUID)){
                newRating = entry.score();
                break;
            }
        }
        Assertions.assertEquals(newRating < oldRating, true);
    }
}
