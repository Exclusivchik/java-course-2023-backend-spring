package edu.java.bot.database;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FakeDataBase {
    private final Map<Long, List<URI>> usersSubscribes;
    private final String unknownUser;

    public void registerUser(Long chatId) {
        usersSubscribes.put(chatId, new ArrayList<>());
    }

    public boolean isRegistered(Long chatId) {
        return usersSubscribes.containsKey(chatId);
    }

    public void addSubscription(Long chatId, URI resource) throws Exception {
        if (isRegistered(chatId)) {
            var resources = usersSubscribes.get(chatId);
            resources.add(resource);
        } else {
            throw new Exception(unknownUser);
        }
    }

    public void removeSubscription(Long chatId, URI resource) throws Exception {
        if (isRegistered(chatId)) {
            var resources = usersSubscribes.get(chatId);
            resources.remove(resource);
        } else {
            throw new Exception(unknownUser);
        }
    }
}
