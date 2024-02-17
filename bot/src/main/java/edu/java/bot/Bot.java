package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
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
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            Long chatId = update.message().chat().id();
            String text = update.message().text();
            if (text == null) {
                continue;
            }
            SendMessage message = initialCommand.handle(chatId, text);
            //SendMessage message = new SendMessage(chatId, "");
            telegramBot.execute(message);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
