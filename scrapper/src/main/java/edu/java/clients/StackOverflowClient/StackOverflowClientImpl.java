package edu.java.clients.StackOverflowClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.Responses.StackOverflowResponse;
import edu.java.retry.RetryGenerator;
import edu.java.retry.RetryPolicy;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClientImpl implements StackOverflowClient {
    @Value(value = "${api.stackoverflow.defaultUrl}")
    private String defaultUrl;
    private final WebClient webClient;
    @Value(value = "${api.stackoverflow.backoffType}")
    private RetryPolicy retryPolicy;
    @Value(value = "${api.stackoverflow.retryCount}")
    private int retryCount;
    @Value(value = "${api.stackoverflow.retryInterval}")
    private int retryInterval;
    @Value(value = "${api.stackoverflow.statuses}")
    private Set<HttpStatus> statuses;
    private Retry retry;

    public StackOverflowClientImpl() {
        webClient = WebClient.builder().baseUrl(defaultUrl).build();
    }

    public StackOverflowClientImpl(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.config(retryPolicy, retryCount, statuses, retryInterval, "stackoverflow-client");
    }

    @Override
    public Optional<StackOverflowResponse> fetchData(Long questionId) {
        return Optional.ofNullable(webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/{id}/answers")
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow")
                .build(questionId))
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parseJson)
            .block());
    }

    @Override
    public Optional<StackOverflowResponse> retryFetchData(long questionId) {
        return Retry.decorateSupplier(retry, () -> fetchData(questionId)).get();
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
