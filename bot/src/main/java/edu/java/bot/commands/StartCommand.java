package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.FakeDataBase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartCommand implements Command {
    private final FakeDataBase fakeDataBase;
    private final Command nextHandler;
    private final String unknownCommand;

    @Override
    public String name() {
        return "/start";
    }

    @Override
    public String description() {
        return "регистрация пользователя";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (text.startsWith(this.name())) {
            if (fakeDataBase.isRegistered(chatId)) {
                return new SendMessage(chatId, "Вы уже зарегистрированы");
            }
            fakeDataBase.registerUser(chatId);
            return new SendMessage(chatId, "Вы успешно зарегистрировались");
        } else if (nextHandler != null) {
            return nextHandler.handle(chatId, text);
        } else {
            return new SendMessage(chatId, unknownCommand);
        }
    }

    @Override
    public Command getNextHandler() {
        return nextHandler;
    }
}
