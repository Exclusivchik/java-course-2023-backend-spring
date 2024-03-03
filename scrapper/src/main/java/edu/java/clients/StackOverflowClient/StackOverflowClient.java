package edu.java.clients.StackOverflowClient;

import edu.java.Responses.StackOverflowResponse;
import java.util.Optional;

public interface StackOverflowClient {
    Optional<StackOverflowResponse> fetchData(Long questionId);
}
