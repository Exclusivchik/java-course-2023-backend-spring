package edu.java.bot.clients;

import edu.java.exceptions.ApiException;
import edu.java.models.AddLinkRequest;
import edu.java.models.ApiErrorResponse;
import edu.java.models.LinkResponse;
import edu.java.models.ListLinksResponse;
import edu.java.models.RemoveLinkRequest;
import edu.java.retry.RetryGenerator;
import edu.java.retry.RetryPolicy;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClient {
    private final String chatPath = "tg-chat/{id}";
    private final String linksUriPath = "/links";
    private final String tgChatIdHeaderName = "Tg-Chat-Id";
    private final WebClient webClient;
    private Retry retry4j;
    @Value(value = "${api.scrapper.retryPolicy}")
    private RetryPolicy policy;
    @Value(value = "${api.scrapper.retryCount}")
    private int count;
    @Value("#{'${api.scrapper.codes}'.split(',')}")
    private Set<HttpStatus> statuses;
    @Value("${api.scrapper.retryInterval}")
    private int interval;
    @Value(value = "${api.scrapper.retryName}")
    private String retryName = "scrapper-client";

    public ScrapperClient(String baseUrl, WebClient.Builder builder) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void configRetry() {
        retry4j = RetryGenerator.config(policy, count, statuses, interval, retryName);
    }

    public void registerChat(Long id) {
        webClient.method(HttpMethod.POST).uri(
                uriBuilder -> uriBuilder.path(chatPath).build(id)
            )
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve().onStatus(
                HttpStatusCode::is4xxClientError,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(String.class)
            .blockOptional();
    }

    public void retryRegisterChat(Long chatId) {
        Retry.decorateRunnable(retry4j, () -> registerChat(chatId)).run();
    }

    public void deleteChat(Long id) {
        webClient.method(HttpMethod.DELETE).uri(
                uriBuilder -> uriBuilder.path(chatPath).build(id)
            )
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve().onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(String.class)
            .blockOptional();
    }

    public void retryDeleteChat(Long chatId) {
        Retry.decorateRunnable(retry4j, () -> deleteChat(chatId)).run();
    }

    public Optional<ListLinksResponse> getLinks(Long id) {
        return webClient.method(HttpMethod.GET)
            .uri(linksUriPath)
            .contentType(MediaType.APPLICATION_JSON)
            .header(tgChatIdHeaderName, String.valueOf(id))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(ListLinksResponse.class)
            .blockOptional();
    }

    public Optional<ListLinksResponse> retryGetLinks(Long chatId) {
        return Retry.decorateSupplier(retry4j, () -> getLinks(chatId)).get();
    }

    public Optional<LinkResponse> addLink(Long id, AddLinkRequest req) {
        return webClient.method(HttpMethod.POST)
            .uri(linksUriPath)
            .contentType(MediaType.APPLICATION_JSON)
            .header(tgChatIdHeaderName, String.valueOf(id))
            .bodyValue(req)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> retryAddLink(Long id, AddLinkRequest req) {
        return Retry.decorateSupplier(retry4j, () -> addLink(id, req)).get();
    }

    public Optional<LinkResponse> deleteLink(Long id, RemoveLinkRequest req) {
        return webClient.method(HttpMethod.DELETE)
            .uri(linksUriPath)
            .contentType(MediaType.APPLICATION_JSON)
            .header(tgChatIdHeaderName, String.valueOf(id))
            .bodyValue(req)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> retryDeleteLink(Long id, RemoveLinkRequest req) {
        return Retry.decorateSupplier(retry4j, () -> deleteLink(id, req)).get();
    }
}
