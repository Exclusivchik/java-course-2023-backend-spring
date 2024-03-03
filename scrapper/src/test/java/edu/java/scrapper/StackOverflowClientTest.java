package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.Responses.StackOverflowResponse;
import edu.java.clients.StackOverflowClient.StackOverflowClient;
import edu.java.clients.StackOverflowClient.StackOverflowClientImpl;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

public class StackOverflowClientTest {
    private WireMockServer wireMockServer;
    private StackOverflowClient stackOverflowClient;

    @BeforeEach
    void init() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        stackOverflowClient = new StackOverflowClientImpl("http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    void stopServer() {
        wireMockServer.stop();
    }

    @Test
    public void fetchLastAnswer() {
        long questionId = 31652353L;
        String responseBody = """
            {
               "items": [
                 {
                   "owner": {
                     "account_id": 52873,
                     "reputation": 2689,
                     "user_id": 158037,
                     "user_type": "registered",
                     "profile_image": "https://www.gravatar.com/avatar/b1b81dd76dea2a6e58fa4c8606301e92?s=256&d=identicon&r=PG",
                     "display_name": "user158037",
                     "link": "https://stackoverflow.com/users/158037/user158037"
                   },
                   "is_accepted": false,
                   "score": 0,
                   "last_activity_date": 1437999326,
                   "creation_date": 1437999326,
                   "answer_id": 31652857,
                   "question_id": 31652353,
                   "content_license": "CC BY-SA 3.0"
                 },
                 {
                   "owner": {
                     "account_id": 23121,
                     "reputation": 528731,
                     "user_id": 57695,
                     "user_type": "registered",
                     "accept_rate": 75,
                     "profile_image": "https://www.gravatar.com/avatar/53ee9941b3fefef67175daf212e62d41?s=256&d=identicon&r=PG",
                     "display_name": "Peter Lawrey",
                     "link": "https://stackoverflow.com/users/57695/peter-lawrey"
                   },
                   "is_accepted": true,
                   "score": 1,
                   "last_activity_date": 1438000148,
                   "creation_date": 1438000148,
                   "answer_id": 31653134,
                   "question_id": 31652353,
                   "content_license": "CC BY-SA 3.0"
                 }
               ],
               "has_more": false,
               "quota_max": 10000,
               "quota_remaining": 9990
            }
            """;
        OffsetDateTime ExpectedLastActivityDate = Instant.ofEpochSecond(1437999326L).atOffset(ZoneOffset.UTC);
        Long expectedQuestionId = 31652353L;
        Long expectedAnswerId = 31652857L;
        String expectedOwnerName = "user158037";
        var uri = UriComponentsBuilder
            .fromPath("/questions/{id}/answers")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("id", questionId));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        StackOverflowResponse response = stackOverflowClient.fetchData(questionId).orElse(null);

        Assertions.assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(ExpectedLastActivityDate, response.lastActivityDate()),
            () -> assertEquals(expectedQuestionId, response.questionId()),
            () -> assertEquals(expectedAnswerId, response.answerId()),
            () -> assertEquals(expectedOwnerName, response.owner().displayName())
        );
    }

    @Test
    public void emptyBody() {
        long questionId = 212958234L;
        String responseBody = "{}";
        var uri = UriComponentsBuilder
            .fromPath("/questions/{id}/answers")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("id", questionId));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        var response = stackOverflowClient.fetchData(questionId);

        assertThat(response).isNotPresent();
    }

    @Test
    public void emptyResponse() {
        long questionId = 21295883L;
        String responseBody = "Igor Goffman";
        var uri = UriComponentsBuilder
            .fromPath("/questions/{id}/answers")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("id", questionId));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        var response = stackOverflowClient.fetchData(questionId);

        assertThat(response).isNotPresent();
    }
}
