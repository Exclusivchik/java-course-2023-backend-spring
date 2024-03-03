package edu.java.dao;

import edu.java.exceptions.ScrapperInvalidRequestException;
import edu.java.exceptions.ScrapperNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RegularUserDao implements UserDao {
    private final Map<Long, Map<Long, URI>> users;

    public RegularUserDao() {
        users = new ConcurrentHashMap<>();
    }

    @Override
    public void registerUser(Long id) {
        if (users.containsKey(id)) {
            throw new ScrapperInvalidRequestException(
                "User with the given chat id is already registered",
                "Пользователь уже зарегистрирован"
            );
        }
        users.put(id, new HashMap<>());
    }

    @Override
    public void deleteUserById(Long id) {
        notFoundCheck(id);
        users.remove(id);
    }

    @Override
    public Map<Long, URI> getLinks(Long id) {
        notFoundCheck(id);
        return users.get(id);
    }

    @Override
    public Long addLink(Long id, URI link) {
        notFoundCheck(id);
        var links = users.get(id);
        if (links.containsValue(link)) {
            throw new ScrapperInvalidRequestException(
                "User with the given chat id is already tracking this link",
                "Пользователь уже отслеживает данную ссылку"
            );
        }
        Long linkId = links.size() + 1L;
        links.put(linkId, link);
        return linkId;
    }

    @Override
    public Long deleteLink(Long id, URI link) {
        notFoundCheck(id);
        var links = users.get(id);
        for (var linkId : links.keySet()) {
            if (links.get(linkId).equals(link)) {
                links.remove(linkId);
                return linkId;
            }
        }
        throw new ScrapperNotFoundException(
            "The user with the given chat id is not tracking this link",
            "Ссылка не найдена"
        );
    }

    private void notFoundCheck(Long id) {
        if (!users.containsKey(id)) {
            throw new ScrapperNotFoundException(
                "User with the given chat id is not exist",
                "Чат не существует"
            );
        }
    }
}
