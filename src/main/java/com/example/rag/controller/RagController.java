package com.example.rag.controller;

import com.example.rag.service.RagService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> processDocument(
            @RequestParam("file") MultipartFile file) {
        try {
            ragService.ingestDocument(file);
            return ResponseEntity.ok("Document processed and embeddings stored successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing document: " + e.getMessage());
        }
    }

    @PostMapping("/query")
    public ResponseEntity<String> queryDocument(
            @RequestParam("query") String query) {
        try {
            String aiResponse = ragService.query(query);
            return ResponseEntity.ok(aiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error querying documents: " + e.getMessage());
        }
    }

    @DeleteMapping("/clean")
    public ResponseEntity<String> cleanDatabase() {
        try {
            ragService.cleanVectorStore();
            return ResponseEntity.ok("Successfully cleared the vector_store table.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to clear database: " + e.getMessage());
        }
    }
}
