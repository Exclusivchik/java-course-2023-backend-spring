package edu.java.configuration;

import edu.java.clients.GitHubClient.GitHubClient;
import edu.java.clients.GitHubClient.GitHubClientImpl;
import edu.java.clients.StackOverflowClient.StackOverflowClient;
import edu.java.clients.StackOverflowClient.StackOverflowClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Bean
    GitHubClient gitHubClient() {
        return new GitHubClientImpl();
    }

    @Bean
    StackOverflowClient stackOverflowClient() {
        return new StackOverflowClientImpl();
    }
}
