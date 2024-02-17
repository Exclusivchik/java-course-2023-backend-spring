package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.database.FakeDataBase;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    private static final String UNKNOWN_COMMAND = "Неизвестная команда";

    @Bean
    TelegramBot telegramBot(@NotNull ApplicationConfig applicationConfig) {
        return new TelegramBot(applicationConfig.telegramToken());
    }

    @Bean
    FakeDataBase fakeDataBase() {
        return new FakeDataBase(new HashMap<>(), "Вы не зарегистрированы");
    }

    @Bean
    Command untrackCommand(FakeDataBase fakeDataBase) {
        return new UntrackCommand(fakeDataBase, null, UNKNOWN_COMMAND);
    }

    @Bean
    Command trackCommand(FakeDataBase fakeDataBase, Command untrackCommand) {
        return new TrackCommand(fakeDataBase, untrackCommand, UNKNOWN_COMMAND);
    }

    @Bean
    Command startCommand(FakeDataBase fakeDataBase, Command trackCommand) {
        return new StartCommand(fakeDataBase, trackCommand, UNKNOWN_COMMAND);
    }

    @Bean
    Command listCommand(FakeDataBase fakeDataBase, Command startCommand) {
        return new ListCommand(fakeDataBase, startCommand, UNKNOWN_COMMAND);
    }

    @Bean
    Command helpCommand(List<Command> commands, Command listCommand) {
        List<String> commandsStrings = commands.stream()
            .map(command -> command.name() + " - " + command.description() + '\n').toList();
        return new HelpCommand(commandsStrings, listCommand, UNKNOWN_COMMAND);
    }
}
