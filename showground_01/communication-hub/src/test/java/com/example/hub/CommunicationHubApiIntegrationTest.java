package com.example.hub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.rate-limit.instant-capacity=20",
        "app.rate-limit.async-capacity=20",
        "app.rate-limit.manual-capacity=20"
})
@AutoConfigureMockMvc
class CommunicationHubApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void versionedChatReturnsTierMetadata() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType("application/json")
                        .content("""
                                {"message":"hello","mode":"native","sessionId":"integration"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reply").value("Hello! How can I help you today?"))
                .andExpect(jsonPath("$.data.mode").value("native"))
                .andExpect(jsonPath("$.rateLimitTier").value("Instant"));
    }

    @Test
    void invalidChatPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType("application/json")
                        .content("""
                                {"message":" ","mode":"native","sessionId":"integration"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void legacyChatRouteKeepsItsOriginalResponseShape() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType("application/json")
                        .content("""
                                {"message":"hello","mode":"native","sessionId":"integration"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Hello! How can I help you today?"))
                .andExpect(jsonPath("$.rateLimitTier").doesNotExist());
    }
}
