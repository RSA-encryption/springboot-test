package com.CODEXIS.PopularDocuments.model;

import java.util.UUID;

public record ScoreEntry(UUID uuid, Float score) {
    public ScoreEntry(UUID uuid, Float score) {
        this.uuid = uuid;
        this.score = score;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public Float score() {
        return score;
    }
}
