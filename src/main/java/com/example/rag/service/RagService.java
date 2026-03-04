package com.example.rag.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

        private final VectorStore vectorStore;
        private final ChatClient chatClient;
        private final JdbcTemplate jdbcTemplate;

        public RagService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder, JdbcTemplate jdbcTemplate) {
                this.vectorStore = vectorStore;
                this.chatClient = chatClientBuilder.build();
                this.jdbcTemplate = jdbcTemplate;
        }

        public void ingestDocument(MultipartFile file) throws IOException {
                // 1. Process PDF and Chunk
                Resource pdfResource = new ByteArrayResource(file.getBytes());
                PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource,
                                PdfDocumentReaderConfig.builder()
                                                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                                                // Skip footers and headers to reduce noise
                                                                .withNumberOfBottomTextLinesToDelete(3)
                                                                .withNumberOfTopTextLinesToDelete(3)
                                                                .withNumberOfTopPagesToSkipBeforeDelete(0)
                                                                .build())
                                                .withPagesPerDocument(1)
                                                .build());

                List<Document> documents = pdfReader.get();

                TokenTextSplitter textSplitter = new TokenTextSplitter(250, 50, 5, 10000, true);
                List<Document> chunkedDocuments = textSplitter.apply(documents);

                // 2. Add embeddings to pgvector
                vectorStore.add(chunkedDocuments);
        }

        public String query(String query) {
                // 3. Retrieve relevant context for the query
                List<Document> relevantContext = vectorStore.similaritySearch(
                                SearchRequest.builder()
                                                .query(query)
                                                .topK(5)
                                                .build());

                if (relevantContext == null) {
                        relevantContext = java.util.List.of();
                }

                String contextString = relevantContext.stream()
                                .map(Document::getText)
                                .collect(Collectors.joining("\n\n"));

                // 4. Send combined context and query to LLM
                String systemPrompt = "You are a helpful assistant. Use the following context extracted from a document to answer the user's question.\n\nContext:\n"
                                + contextString;

                return chatClient.prompt()
                                .system(systemPrompt)
                                .user(query)
                                .call()
                                .content();
        }

        public void cleanVectorStore() {
                jdbcTemplate.execute("TRUNCATE TABLE vector_store");
        }
}
