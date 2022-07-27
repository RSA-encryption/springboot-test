package com.CODEXIS.PopularDocuments.model;

import java.sql.Timestamp;
import java.util.UUID;

public record Visit(UUID uuid, User by, Timestamp at) {
    public Visit(UUID uuid, User by, Timestamp at) {
        this.uuid = uuid;
        this.by = by;
        this.at = at;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public User by() {
        return by;
    }

    @Override
    public Timestamp at() {
        return at;
    }
}
