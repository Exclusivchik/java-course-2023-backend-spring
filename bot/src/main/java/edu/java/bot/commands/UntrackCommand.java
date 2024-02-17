package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.FakeDataBase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private final FakeDataBase fakeDataBase;
    private final Command nextHandler;
    private final String unknownCommand;

    @Override
    public String name() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "прекратить отслеживать ресурс";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (text.startsWith(this.name())) {
            return new SendMessage(chatId, "Заглушка для untrackCommand");
        } else if (nextHandler != null) {
            return nextHandler.handle(chatId, text);
        } else {
            return new SendMessage(chatId, unknownCommand);
        }
    }
}
