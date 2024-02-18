package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.linkvalidators.LinkValidator;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private final Command nextHandler;
    private final LinkValidator linkValidator;
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
            String link = text.substring(this.name().length()).strip();
            String scheme = "https://";
            String fullUri = link;
            if (!link.startsWith(scheme)) {
                fullUri = scheme + link;
            }
            try {
                URI uri = new URI(fullUri);
                if (linkValidator.isValid(uri)) {
                    return new SendMessage(chatId, "Вы успешно отписались от ресурса");
                }
            } catch (URISyntaxException e) {
            }
            return new SendMessage(chatId, "Проверьте корректность ссылки");
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
