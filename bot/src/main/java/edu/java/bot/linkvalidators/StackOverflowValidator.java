package edu.java.bot.linkvalidators;

import java.net.URI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StackOverflowValidator implements LinkValidator {
    private final LinkValidator nextHandler;

    @Override
    public boolean isValid(URI uri) {
        if (uri.getHost().equals("stackoverflow.com") && !uri.getPath().isEmpty()) {
            return true;
        } else if (nextHandler != null) {
            return nextHandler.isValid(uri);
        } else {
            return false;
        }
    }
}
