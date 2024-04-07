package edu.java.retry;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public final class RetryGenerator {
    private RetryGenerator() {
    }

    public static Retry config(
        RetryPolicy retryPolicy,
        int count,
        Set<HttpStatus> statuses,
        int interval,
        String retryName
    ) {
        RetryConfig config = switch (retryPolicy) {
            case CONSTANT -> constant(count, statuses, interval);
            case LINEAR -> linear(count, statuses, interval);
            case EXPONENTIAL -> exponential(count, statuses);
        };

        return Retry.of(retryName + OffsetDateTime.now(), config);
    }

    private static RetryConfig constant(int count, Set<HttpStatus> statuses, int interval) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(count)
            .waitDuration(Duration.ofSeconds(interval))
            .retryOnException(
                ex -> ex instanceof WebClientResponseException
                    && statuses.contains(((WebClientResponseException) ex).getStatusCode())
            )
            .build();
    }

    private static RetryConfig linear(int count, Set<HttpStatus> statuses, int interval) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(count)
            .intervalFunction(IntervalFunction.of(
                Duration.ofSeconds(interval),
                attempt -> interval + attempt * interval
            ))
            .retryOnException(
                ex -> ex instanceof WebClientResponseException
                    && statuses.contains(((WebClientResponseException) ex).getStatusCode())
            )
            .build();
    }

    private static RetryConfig exponential(int count, Set<HttpStatus> statuses) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(count)
            .intervalFunction(IntervalFunction.ofExponentialBackoff(
                IntervalFunction.DEFAULT_INITIAL_INTERVAL,
                IntervalFunction.DEFAULT_MULTIPLIER
            ))
            .retryOnException(
                ex -> ex instanceof WebClientResponseException
                    && statuses.contains(((WebClientResponseException) ex).getStatusCode())
            )
            .build();
    }
}
