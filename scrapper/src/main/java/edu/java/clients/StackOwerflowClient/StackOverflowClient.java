package edu.java.clients.StackOwerflowClient;

import edu.java.Responses.StackOverflowResponse;
import java.util.Optional;

public interface StackOverflowClient {
    Optional<StackOverflowResponse> fetchData(Long questionId);
}
