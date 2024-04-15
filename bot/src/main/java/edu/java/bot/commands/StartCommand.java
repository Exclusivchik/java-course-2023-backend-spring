package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.exceptions.ApiException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartCommand implements Command {
    private final Command nextHandler;
    private final ScrapperClient scrapperClient;
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
        if (text.equals(this.name())) {
            try {
                scrapperClient.retryRegisterChat(chatId);
            } catch (ApiException e) {
                return new SendMessage(chatId, e.getDescription());
            }
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
