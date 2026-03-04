# DeepParchment 📜

**DeepParchment** is a private, fully local Retrieval-Augmented Generation (RAG) backend API built with Spring Boot and Spring AI. It allows you to ingest personal PDF documents, store them securely in a pgvector database, and query them using natural language via local LLMs. 

## ✨ Features
* **PDF Ingestion:** Automatically parses, chunks, and indexes local PDF files via Spring AI's `PagePdfDocumentReader` and `TokenTextSplitter`.
* **Parallel Processing:** Uses parallel streams for high-performance batch embedding insertions.
* **Semantic Search:** Leverages `pgvector` and PostgreSQL to find the most relevant context for your questions using cosine distance (`hnsw`).
* **100% Local & Private:** Powered by Ollama running open-source models (Llama 3.2 3B for chat, Nomic Embed Text for embeddings). Your data never leaves your machine.
* **Encrypted Secrets:** Database credentials are encrypted using Jasypt.

## 🛠️ Tech Stack
* **Framework:** Spring Boot 3.4.x
* **AI Integration:** Spring AI (`spring-ai-ollama`, `spring-ai-pgvector-store`)
* **Database:** PostgreSQL with `pgvector`
* **Local Inference:** Ollama
* **Security:** Jasypt (Java Simplified Encryption)

## 🚀 Getting Started

### Prerequisites
1. **Java 21** or higher.
2. **Maven**.
3. **PostgreSQL** with the `pgvector` extension installed (running on `localhost:5432/pdf_db`).
4. **Ollama** installed and running locally on `http://localhost:11434`.

### Required Ollama Models
Before running the application, you must pull the required models using Ollama:
```bash
ollama pull llama3.2:3b
ollama pull nomic-embed-text
```

### Environment Variables
The application uses environment variables for secure database access and Jasypt decryption. You must provide these variables when running the application.

* `DB_PASSWORD`: Your PostgreSQL database password.
* `JASYPT_ENCRYPTOR_PASSWORD`: The master key used to decrypt any encrypted properties.

### Installation & Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ayush-ksingh/DeepParchment.git
   cd DeepParchment
   ```

2. **Run the Application locally:**
   ```bash
   DB_PASSWORD=your_actual_db_password JASYPT_ENCRYPTOR_PASSWORD=your_secret_key mvn spring-boot:run
   ```

## 📚 API Endpoints

### 1. Ingest a Document
Upload a PDF file to be parsed, chunked, and embedded into the database.
* **Endpoint:** `POST /api/rag/process`
* **Content-Type:** `multipart/form-data`
* **Body:** `file` (Your `.pdf` file)

### 2. Query the Assistant
Ask a question based on your ingested documents.
* **Endpoint:** `POST /api/rag/query`
* **Content-Type:** `application/json`
* **Body:** 
  ```json
  {
    "query": "What are the main topics discussed in the document?"
  }
  ```
