package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.FakeDataBase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrackCommand implements Command {
    private final FakeDataBase fakeDataBase;
    private final Command nextHandler;
    private final String unknownCommand;
    @Override
    public String name() {
        return "/track";
    }

    @Override
    public String description() {
        return "начать отслеживать ресурс";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (text.startsWith(this.name())) {
            return new SendMessage(chatId, "Заглушка для trackCommand");
        } else if (nextHandler != null) {
            return nextHandler.handle(chatId, text);
        } else {
            return new SendMessage(chatId, unknownCommand);
        }
    }
}
