package edu.java.clients.GitHubClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.Responses.GitHubResponse;
import edu.java.retry.RetryGenerator;
import edu.java.retry.RetryPolicy;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClientImpl implements GitHubClient {
    @Value(value = "${api.github.defaultUrl}")
    private String defaultUrl;
    private final WebClient webClient;
    @Value(value = "${api.github.backoffType}")
    private RetryPolicy retryPolicy;
    @Value(value = "${api.github.retryCount}")
    private int retryCount;
    @Value(value = "${api.github.retryInterval}")
    private int retryInterval;
    @Value(value = "#{'${api.github.statuses}'.split(',')}")
    private Set<HttpStatus> statuses;
    private Retry retry;

    public GitHubClientImpl() {
        webClient = WebClient.builder().baseUrl(defaultUrl).build();
    }

    public GitHubClientImpl(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.config(retryPolicy, retryCount, statuses, retryInterval, "github-client");
    }

    @Override
    public Optional<GitHubResponse> fetchData(String owner, String repo) {
        return Optional.ofNullable(webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/repos/{owner}/{repo}/events")
                .queryParam("per_page", 1)
                .build(owner, repo))
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parseJson)
            .block()
        );
    }

    @Override
    public Optional<GitHubResponse> retryFetchRepositoryEvents(String owner, String repo) {
        return Retry.decorateSupplier(retry, () -> retryFetchRepositoryEvents(owner, repo)).get();
    }

    public GitHubResponse parseJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            List<GitHubResponse> responseList = objectMapper.readValue(json, new TypeReference<>() {
            });
            return responseList.getFirst();
        } catch (Exception e) {
            return null;
        }
    }
}
