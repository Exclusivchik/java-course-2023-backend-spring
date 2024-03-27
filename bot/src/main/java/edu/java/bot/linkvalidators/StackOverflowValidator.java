package edu.java.bot.linkvalidators;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StackOverflowValidator implements LinkValidator {
    private final LinkValidator nextHandler;

    @Override
    public boolean isValid(String sUri) {
        try {
            URI uri = new URI(sUri);
            if (uri.getHost().equals("stackoverflow.com") && !uri.getPath().isEmpty()) {
                return true;
            } else if (nextHandler != null) {
                return nextHandler.isValid(sUri);
            } else {
                return false;
            }
        } catch (URISyntaxException e) {
            return false;
        }

    }
}
