package edu.java.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StackOverflowResponse(
    Owner owner,
    @JsonProperty("last_activity_date")
    OffsetDateTime lastActivityDay,
    @JsonProperty("answer_id")
    Long answerId,
    @JsonProperty("question_id")
    Long questionId
) {
    public record Owner(
        @JsonProperty("display_name")
        String displayName
    ) {

    }
}
