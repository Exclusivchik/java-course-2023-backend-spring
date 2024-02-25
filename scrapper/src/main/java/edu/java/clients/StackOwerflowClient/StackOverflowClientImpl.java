package edu.java.clients.StackOwerflowClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.Responses.StackOverflowResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClientImpl implements StackOverflowClient {
    @Value(value = "${api.stackoverflow.defaultUrl}")
    private String defaultUrl;
    private final WebClient webClient;

    public StackOverflowClientImpl() {
        webClient = WebClient.builder().baseUrl(defaultUrl).build();
    }

    @Override
    public Optional<StackOverflowResponse> fetchData(Long questionId) {
        return Optional.ofNullable(webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/{id}/answers")
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow")
                .build(questionId)
            )
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parseJson)
            .block()
        );
    }

    public StackOverflowResponse parseJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode lastAnswer = root.get("items").get(0);
            return objectMapper.treeToValue(lastAnswer, StackOverflowResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
}
