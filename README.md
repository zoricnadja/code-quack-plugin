# Code Quack

**Code Quack** is a "Rubber Duck Debugging" tool integrated directly into IntelliJ IDEA. Instead of simply providing the solution, this AI assistant (powered by OpenAI) acts like a Socratic mentor—it asks probing questions to help you spot errors in your logic and debug your code yourself.

## System Architecture

The project consists of two main components:
1.  **Backend (Java/Spring Boot):** Acts as middleware that communicates with the OpenAI API, manages system prompts, and handles the chat context.
2.  **IntelliJ Plugin (Java/Swing):** A client-side plugin that allows developers to select code, ask questions, and receive feedback directly within the IDE.

## Features

* **Socratic Debugging:** The AI is instructed not to fix the code immediately but to ask questions about assumptions, variable states, and execution flow.
* **Context Awareness:** The plugin automatically extracts the selected code snippet from the editor and sends it to the backend for analysis.
* **Conversation History:** Both the Plugin and Backend support stateful conversations, allowing for multi-turn dialogues about a specific problem.
* **IDE Integration:** seamless access via **Right-click -> "Ask Rubber Duck"** or the dedicated Tool Window.
* **Custom Input:** A popup dialog allows you to ask specific questions (e.g., *"Explain this loop"* or *"Why is this null?"*).
* **Audio Feedback:** Fun "Quack" sound effect 🦆 upon interaction.

## Tech Stack

### Backend
* **Java:** 21
* **Framework:** Spring Boot 3.4.x (using WebFlux/WebClient)
* **Build Tool:** Maven
* **AI:** OpenAI API (GPT-4o-mini)

### Plugin
* **SDK:** IntelliJ Platform SDK
* **Language:** Java
* **UI:** Swing
* **Build Tool:** Gradle

---

## Installation & Setup

### 1. Backend Setup

1.  Navigate to the `backend` directory.
2.  Configure your OpenAI API Key in `src/main/resources/application.properties`:
    ```properties
    openai.api.key=YOUR_OPENAI_SK_KEY_HERE
    server.port=8080
    ```
3.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
    *The server will start at `http://localhost:8080`.*

### 2. Plugin Setup

1.  Open the `plugin` directory in IntelliJ IDEA.
2.  Allow Gradle to import dependencies.
3.  To run and test the plugin, use the Gradle task:
    * **Gradle** -> **intellij** -> **runIde**
    * This will launch a new instance of IntelliJ IDEA with the plugin installed.

---

## How to Use

1.  **Select Code:** Highlight a snippet of code in your editor that is causing issues.
2.  **Ask the Duck:** Right-click and select **"Ask Rubber Duck"** (or use the Tool Window).
3.  **Prompt:** A popup dialog will appear. Enter your specific question or use the default *"What is wrong with this code?"*.
4.  **Debug:** The Duck will quack, analyze your code, and respond with a guiding question in the chat panel.
