package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.linkvalidators.GithubLinkValidator;
import edu.java.bot.linkvalidators.LinkValidator;
import edu.java.bot.linkvalidators.StackOverflowValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {
    private static final String UNKNOWN_COMMAND = "Неизвестная команда";

    @Bean
    TelegramBot telegramBot(ApplicationConfig applicationConfig) {
        return new TelegramBot(applicationConfig.telegramToken());
    }

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    ScrapperClient scrapperClient(
        @Value(value = "${api.scrapper.defaultUrl}") String defaultScrapperUrl,
        WebClient.Builder webClientBuilder
    ) {
        return new ScrapperClient(defaultScrapperUrl, webClientBuilder);
    }

    @Bean
    GithubLinkValidator githubLinkValidator() {
        return new GithubLinkValidator(null);
    }

    @Bean
    StackOverflowValidator stackOverflowValidator(LinkValidator githubLinkValidator) {
        return new StackOverflowValidator(githubLinkValidator);
    }

    @Bean
    Command untrackCommand(LinkValidator stackOverflowValidator, ScrapperClient scrapperClient) {
        return new UntrackCommand(null, stackOverflowValidator, scrapperClient, UNKNOWN_COMMAND);
    }

    @Bean
    Command trackCommand(LinkValidator stackOverflowValidator, Command untrackCommand, ScrapperClient scrapperClient) {
        return new TrackCommand(untrackCommand, stackOverflowValidator, scrapperClient, UNKNOWN_COMMAND);
    }

    @Bean
    Command startCommand(Command trackCommand, ScrapperClient scrapperClient) {
        return new StartCommand(trackCommand, scrapperClient, UNKNOWN_COMMAND);
    }

    @Bean
    Command listCommand(Command startCommand, ScrapperClient scrapperClient) {
        return new ListCommand(startCommand, scrapperClient, UNKNOWN_COMMAND);
    }

    @Bean
    Command helpCommand(List<Command> commands, Command listCommand) {
        List<String> commandsStrings = commands.stream()
            .map(command -> command.name() + " - " + command.description() + '\n').toList();
        return new HelpCommand(commandsStrings, listCommand, UNKNOWN_COMMAND);
    }
}
