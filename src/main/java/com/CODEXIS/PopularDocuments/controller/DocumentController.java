package com.CODEXIS.PopularDocuments.controller;

import com.CODEXIS.PopularDocuments.model.Document;
import com.CODEXIS.PopularDocuments.model.Score;
import com.CODEXIS.PopularDocuments.model.User;
import com.CODEXIS.PopularDocuments.repository.DocumentRepository;
import com.CODEXIS.PopularDocuments.repository.UserRepository;
import com.CODEXIS.PopularDocuments.repository.VisitRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public final class DocumentController {
    private final DocumentRepository documentRepository;
    private final VisitRepository visitRepository;
    private final UserRepository userRepository;

    public DocumentController(DocumentRepository documentRepository, VisitRepository visitRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/visit")
    @ResponseBody
    public ResponseEntity<String> visit(@RequestParam(name = "docUUID") String docUUID) {
        return visitRepository.visit(UUID.fromString(docUUID));
    }

    @QueryMapping
    public List<Document> getDocument(@Argument String uuid) {
        return documentRepository.getDocument(UUID.fromString(uuid));
    }

    @QueryMapping
    public List<User> getUser(@Argument String uuid) {
        return userRepository.getUser(UUID.fromString(uuid));
    }

    @QueryMapping
    public List<Score> getScore(@Argument Integer limit, @Argument Boolean useRelativeVariance, @Argument String to, @Argument String from) {
        return visitRepository.getScore(limit, useRelativeVariance, from, to);
    }
}
