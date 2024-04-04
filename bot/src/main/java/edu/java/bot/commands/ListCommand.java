package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.exceptions.ApiException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListCommand implements Command {
    private final Command nextHandler;
    private final ScrapperClient scrapperClient;
    private final String unknownCommand;

    @Override
    public String name() {
        return "/list";
    }

    @Override
    public String description() {
        return "Список подписок";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (text.equals(this.name())) {
            try {
                List<URI> links = scrapperClient.getLinks(chatId).get().links().stream()
                    .map(LinkResponse::url).toList();
                if (links.isEmpty()) {
                    return new SendMessage(chatId, "Вы ни на что не подписаны");
                }
                StringBuilder stringBuilder = new StringBuilder("Ваши подписки:\n");
                for (var link : links) {
                    stringBuilder.append(link.toString()).append('\n');
                }
                return new SendMessage(chatId, stringBuilder.toString());
            } catch (ApiException e) {
                return new SendMessage(chatId, e.getDescription());
            }
        } else {
            SendMessage sendMessage;
            if (nextHandler != null) {
                sendMessage = nextHandler.handle(chatId, text);
            } else {
                sendMessage = new SendMessage(chatId, unknownCommand);
            }
            return sendMessage;
        }
    }

    @Override
    public Command getNextHandler() {
        return nextHandler;
    }
}
