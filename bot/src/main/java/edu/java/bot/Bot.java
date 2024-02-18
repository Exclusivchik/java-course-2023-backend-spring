package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Bot implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final Command initialCommand;

    public Bot(TelegramBot telegramBot, Command helpCommand) {
        this.telegramBot = telegramBot;
        this.initialCommand = helpCommand;
        telegramBot.setUpdatesListener(this);
        telegramBot.execute(createCommandMenu());
    }

    private SetMyCommands createCommandMenu() {
        List<Command> commands = new ArrayList<>();
        Command temp = initialCommand;
        while (temp != null) {
            commands.add(temp);
            temp = temp.getNextHandler();
        }

        return new SetMyCommands(
            commands.stream().map(command -> new BotCommand(
                command.name(),
                command.description()
            )).toArray(BotCommand[]::new)
        );
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            if (update.message() == null) {
                continue;
            }
            Long chatId = update.message().chat().id();
            String text = update.message().text();

            SendMessage message = initialCommand.handle(chatId, text);
            telegramBot.execute(message);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
