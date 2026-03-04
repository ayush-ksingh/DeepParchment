# DeepParchment 📜

**DeepParchment** is a private document retrieval backend assistant built in Java. It allows you to point the application at your personal PDF documents and query them using natural language, effectively giving your files a "voice" through Retrieval-Augmented Generation (RAG).

## ✨ Features
* **PDF Ingestion:** Automatically parses and indexes local PDF files using LangChain4j.
* **Vector Search:** Uses semantic search to find the most relevant context for your questions.
* **Privacy-First:** Designed to run against your local data, ensuring your private documents stay under your control.

## 🚀 Getting Started

### Prerequisites
* **Java 17** or higher
* **Maven** (or use the included `./mvnw` wrapper)
* An API Key for your provider (OpenAI, Gemini, or a local Ollama instance)

### Installation
1. Clone the repository:
   ```bash
   git clone [https://github.com/ayush-ksingh/DeepParchment.git](https://github.com/ayush-ksingh/DeepParchment.git)
   cd DeepParchment
