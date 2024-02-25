package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.Responses.GitHubResponse;
import edu.java.clients.GitHubClient.GitHubClient;
import edu.java.clients.GitHubClient.GitHubClientImpl;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GitHubClientTest {
    private WireMockServer wireMockServer;
    private GitHubClient gitHubClient;

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        gitHubClient = new GitHubClientImpl("http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    void stopServer() {
        wireMockServer.stop();
    }

    @Test
    public void fetchLastEvent() {
        String ownerName = "Exclusivchik";
        String repoName = "Exclusivchik/java-course-2023-backend-spring";
        String responseBody =
            """
                    [
                      {
                        "id": "35988322233",
                        "type": "PushEvent",
                        "actor": {
                          "id": 110172025,
                          "login": "Exclusivchik",
                          "display_login": "Exclusivchik",
                          "gravatar_id": "",
                          "url": "https://api.github.com/users/Exclusivchik",
                          "avatar_url": "https://avatars.githubusercontent.com/u/110172025?"
                        },
                        "repo": {
                          "id": 755528647,
                          "name": "Exclusivchik/java-course-2023-backend-spring",
                          "url": "https://api.github.com/repos/Exclusivchik/java-course-2023-backend-spring"
                        },
                        "payload": {
                          "repository_id": 755528647,
                          "push_id": 17258710507,
                          "size": 1,
                          "distinct_size": 1,
                          "ref": "refs/heads/hw2",
                          "head": "eff5676a350a454382412e802f4e7e8f8df45bcd",
                          "before": "93a6ede2409a8d8c55f0f4c4d310251fdf8d742f",
                          "commits": [
                            {
                              "sha": "eff5676a350a454382412e802f4e7e8f8df45bcd",
                              "author": {
                                "email": "damirbekirov2004@mail.ru",
                                "name": "Damirem"
                              },
                              "message": "Второе ДЗ без тестов",
                              "distinct": true,
                              "url": "https://api.github.com/repos/Exclusivchik/java-course-2023-backend-spring/commits/eff5676a350a454382412e802f4e7e8f8df45bcd"
                            }
                          ]
                        },
                        "public": true,
                        "created_at": "2024-02-25T19:01:26Z"
                      }
                    ]
                """;
        Long expectedId = 35988322233L;
        String expectedType = "PushEvent";
        String expectedActorName = "Exclusivchik";
        String expectedRepoName = "Exclusivchik/java-course-2023-backend-spring";
        OffsetDateTime expectedCreatedAt = OffsetDateTime.parse("2024-02-25T19:01:26Z");
        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner", ownerName,
                "repo", repoName
            ));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        GitHubResponse response = gitHubClient.fetchData(ownerName, repoName).orElse(null);

        Assertions.assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(expectedId, response.id()),
            () -> assertEquals(expectedType, response.type()),
            () -> assertEquals(expectedActorName, response.actor().login()),
            () -> assertEquals(expectedRepoName, response.repo().name()),
            () -> assertEquals(expectedCreatedAt, response.createdAt())
        );
    }

    @Test
    public void emptyBody() {
        String ownerName = "Exclusivchik";
        String repoName = "Exclusivchik/java-course-2023-backend-spring";
        String responseBody = "[]";
        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner", ownerName,
                "repo", repoName
            ));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        var response = gitHubClient.fetchData(ownerName, repoName);

        assertThat(response).isNotPresent();
    }

    @Test
    public void emptyResponse() {
        String ownerName = "Exclusivchik";
        String repoName = "Exclusivchik/java-course-2023-backend-spring";
        String responseBody = "Igor Goffman";
        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner", ownerName,
                "repo", repoName
            ));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        var response = gitHubClient.fetchData(ownerName, repoName);

        assertThat(response).isNotPresent();
    }
}
