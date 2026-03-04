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
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "dummy content".getBytes());

        doNothing().when(vectorStore).add(anyList());

        try (org.mockito.MockedConstruction<org.springframework.ai.reader.pdf.PagePdfDocumentReader> mockedReader = mockConstruction(
                org.springframework.ai.reader.pdf.PagePdfDocumentReader.class,
                (mock, context) -> when(mock.get()).thenReturn(List.of(new Document("Mock PDF Content"))));
                org.mockito.MockedConstruction<org.springframework.ai.transformer.splitter.TokenTextSplitter> mockedSplitter = mockConstruction(
                        org.springframework.ai.transformer.splitter.TokenTextSplitter.class,
                        (mock, context) -> when(mock.apply(anyList()))
                                .thenReturn(List.of(new Document("Mock Chunk"))))) {

            ragService.ingestDocument(file);
        }

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
