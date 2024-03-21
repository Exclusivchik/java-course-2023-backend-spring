package edu.java.api.domain.repository;

import edu.java.api.domain.dto.LinkChatMappingDto;
import edu.java.models.LinkResponse;
import java.util.List;

public interface LinkChatMappingRepository {
    void add(Long chatId, Long linkId);

    void remove(Long chatId, Long linkId);

    List<LinkChatMappingDto> findAll();

    List<LinkResponse> findAllByChatId(Long chatId);

    List<LinkChatMappingDto> findAllByLinkId(Long linkId);

}
