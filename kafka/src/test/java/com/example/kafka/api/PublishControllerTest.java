package com.example.kafka.api;
import com.example.kafka.infra.MessagePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublishController.class)
class PublishControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockitoBean
    MessagePublisher publisher;
    @Test
    void publishEndpointReturnsAccepted() throws Exception {
        when(publisher.publish(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
        var body = new MessageRequest("k1", "v1");
        mvc.perform(post("/api/messages")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("queued"));
        verify(publisher).publish("k1", "v1");
    }
}