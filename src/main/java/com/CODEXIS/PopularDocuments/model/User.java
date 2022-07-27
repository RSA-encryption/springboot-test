package com.CODEXIS.PopularDocuments.model;

import java.sql.Timestamp;
import java.util.UUID;

public record User(UUID uuid, String name, Timestamp createdAt) {
    public User(UUID uuid, String name, Timestamp createdAt) {
        this.uuid = uuid;
        this.name = name;
        this.createdAt = createdAt;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Timestamp createdAt() {
        return createdAt;
    }
}