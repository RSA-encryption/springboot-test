package com.CODEXIS.PopularDocuments.model;

import java.util.List;

public record Score(List<ScoreEntry> entries, Float variance) {
    public Score(List<ScoreEntry> entries, Float variance) {
        this.entries = entries;
        this.variance = variance;
    }

    @Override
    public List<ScoreEntry> entries() {
        return entries;
    }

    @Override
    public Float variance() {
        return variance;
    }
}
