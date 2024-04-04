package edu.java.api.service.jdbc;

import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkChatMappingRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.exceptions.BadRequestException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcChatRepository jdbcChatRepository;
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcLinkChatMappingRepository jdbclinkChatMappingRepository;

    @Override
    public LinkResponse add(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        isValidUri(url);
        Long linkId = (jdbcLinkRepository.isExist(url))
            ? jdbcLinkRepository.findByUrl(url).linkId()
            : jdbcLinkRepository.add(url);
        jdbclinkChatMappingRepository.add(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public LinkResponse remove(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        Long linkId = jdbcLinkRepository.findByUrl(url).linkId();
        jdbclinkChatMappingRepository.remove(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public List<LinkResponse> listAll(long tgChatId) {
        registrationValidation(tgChatId);
        return jdbclinkChatMappingRepository.findAllByChatId(tgChatId);
    }

    private void isValidUri(URI uri) {
        if (!uri.getHost().equals("github.com") && !uri.getHost().equals("stackoverflow.com")) {
            throw new BadRequestException(
                "That resources is not supported",
                "Данный ресурс не поддерживается!"
            );
        }
    }

    private void registrationValidation(Long tgChatId) {
        if (jdbcChatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                "The user with the given chat id is not registered",
                "Пользователь не зарегистрирован"
            );
        }
    }
}
