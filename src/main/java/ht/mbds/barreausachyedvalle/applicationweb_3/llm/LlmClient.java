package ht.mbds.barreausachyedvalle.applicationweb_3.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;
import java.io.Serializable;

@Dependent
public class LlmClient implements Serializable {

    private String systemRole;
    private Assistant assistant;
    private ChatMemory chatMemory;
    private int totalTokens = 0;

    public LlmClient() {
        String apiKey = System.getenv("GEMINI_KEY");

        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .build();

        this.chatMemory = MessageWindowChatMemory.withMaxMessages(20);

        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    public void setSystemRole(String role) {
        this.systemRole = role;
        this.chatMemory.clear();
        this.chatMemory.add(SystemMessage.from(role));
    }

    public String envoyerQuestion(String role, String question) {
        if (role != null) {
            setSystemRole(role);
            totalTokens += role.length() / 4;
        }

        totalTokens += question.length() / 4;

        String reponse = assistant.chat(question);

        totalTokens += reponse.length() / 4;

        return reponse;
    }
    public int getTotalTokens() {
        return totalTokens;
    }

}