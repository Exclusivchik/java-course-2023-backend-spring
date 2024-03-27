package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.linkvalidators.LinkValidator;
import edu.java.exceptions.ApiException;
import edu.java.models.RemoveLinkRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private final Command nextHandler;
    private final LinkValidator linkValidator;
    private final ScrapperClient scrapperClient;
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
            String fullUri = text.substring(this.name().length()).strip();
            String scheme = "https://";
            if (!fullUri.startsWith(scheme)) {
                fullUri = scheme + fullUri;
            }
            if (linkValidator.isValid(fullUri)) {
                URI link = URI.create(fullUri);
                try {
                    scrapperClient.deleteLink(chatId, new RemoveLinkRequest(link));
                    return new SendMessage(chatId, "Подписка отменена");
                } catch (ApiException e) {
                    return new SendMessage(chatId, e.getDescription());
                }
            } else {
                return new SendMessage(chatId, "Ссылка некорректна");
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
