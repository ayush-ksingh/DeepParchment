package com.example.rag.controller;

import com.example.rag.service.RagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RagController.class)
class RagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RagService ragService;

    @Test
    void processDocument_ShouldReturnSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "dummy content".getBytes());

        doNothing().when(ragService).ingestDocument(any());

        mockMvc.perform(multipart("/api/rag/process")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Document processed and embeddings stored successfully."));
    }

    @Test
    void queryDocument_ShouldReturnResponse() throws Exception {
        String query = "What is RAG?";
        String aiResponse = "RAG stands for Retrieval-Augmented Generation.";

        when(ragService.query(anyString())).thenReturn(aiResponse);

        mockMvc.perform(post("/api/rag/query")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(content().string(aiResponse));
    }

    @Test
    void cleanDatabase_ShouldReturnSuccess() throws Exception {
        doNothing().when(ragService).cleanVectorStore();

        mockMvc.perform(delete("/api/rag/clean"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully cleared the vector_store table."));
    }
}
