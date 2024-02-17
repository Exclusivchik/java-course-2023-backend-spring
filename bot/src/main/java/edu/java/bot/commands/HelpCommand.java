package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class HelpCommand implements Command {
    private final List<String> commands;
    private final Command nextHandler;
    private final String unknownCommand;

    @Override
    public String name() {
        return "/help";
    }

    @Override
    public String description() {
        return "Все команды";
    }

    @Override
    public SendMessage handle(long chatId, String message) {
        if (message.equals(this.name())) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String command : commands) {
                stringBuilder.append(command);
            }
            return new SendMessage(chatId, stringBuilder.toString());
        } else if (nextHandler != null) {
            return nextHandler.handle(chatId, message);
        } else {
            return new SendMessage(chatId, unknownCommand);
        }
    }
}
