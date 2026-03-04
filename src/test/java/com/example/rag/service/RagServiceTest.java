package com.example.rag.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RagServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private RagService ragService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        ragService = new RagService(vectorStore, chatClientBuilder, jdbcTemplate);
    }

    @Test
    void ingestDocument_ShouldParseAndStore() throws IOException {
        String dummyPdfContent = "%PDF-1.4\n" +
                "1 0 obj\n" +
                "<< /Type /Catalog /Pages 2 0 R >>\n" +
                "endobj\n" +
                "2 0 obj\n" +
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n" +
                "endobj\n" +
                "3 0 obj\n" +
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>\n"
                +
                "endobj\n" +
                "4 0 obj\n" +
                "<< /Length 44 >>\n" +
                "stream\n" +
                "BT\n" +
                "/F1 24 Tf\n" +
                "100 700 Td\n" +
                "(Hello World) Tj\n" +
                "ET\n" +
                "endstream\n" +
                "endobj\n" +
                "5 0 obj\n" +
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n" +
                "endobj\n" +
                "xref\n" +
                "0 6\n" +
                "0000000000 65535 f \n" +
                "0000000009 00000 n \n" +
                "0000000058 00000 n \n" +
                "0000000115 00000 n \n" +
                "0000000222 00000 n \n" +
                "0000000315 00000 n \n" +
                "trailer\n" +
                "<< /Size 6 /Root 1 0 R >>\n" +
                "startxref\n" +
                "403\n" +
                "%%EOF";

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", dummyPdfContent.getBytes());

        doNothing().when(vectorStore).add(anyList());

        ragService.ingestDocument(file);

        verify(vectorStore, times(1)).add(anyList());
    }

    @Test
    void query_ShouldReturnResponse() {
        String query = "Test Question";
        String expectedResponse = "Test Answer";
        Document mockDoc = new Document("Test Context");

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(mockDoc));

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedResponse);

        String result = ragService.query(query);

        assertEquals(expectedResponse, result);
        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
        verify(chatClient, times(1)).prompt();
    }

    @Test
    void cleanVectorStore_ShouldTruncateTable() {
        ragService.cleanVectorStore();
        verify(jdbcTemplate, times(1)).execute("TRUNCATE TABLE vector_store");
    }
}
