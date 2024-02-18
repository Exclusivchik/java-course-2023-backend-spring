package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.linkvalidators.LinkValidator;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrackCommand implements Command {
    private final Command nextHandler;
    private final LinkValidator linkValidator;
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
            String link = text.substring(this.name().length()).strip();
            String scheme = "https://";
            String fullUri = link;
            if (!link.startsWith(scheme)) {
                fullUri = scheme + link;
            }
            try {
                URI uri = new URI(fullUri);
                if (linkValidator.isValid(uri)) {
                    return new SendMessage(chatId, "Ссылка успешно добавлена");
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
