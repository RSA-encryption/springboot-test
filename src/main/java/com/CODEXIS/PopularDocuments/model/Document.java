package com.CODEXIS.PopularDocuments.model;

import java.sql.Timestamp;
import java.util.UUID;

public record Document(UUID uuid, User user, Timestamp createdAt) {
    public Document(UUID uuid, User user, Timestamp createdAt) {
        this.uuid = uuid;
        this.user = user;
        this.createdAt = createdAt;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public Timestamp createdAt() {
        return createdAt;
    }
}
