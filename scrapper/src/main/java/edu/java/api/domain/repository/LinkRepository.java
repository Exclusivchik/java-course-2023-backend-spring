package edu.java.api.domain.repository;

import edu.java.api.domain.dto.LinkDto;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkRepository {
    Long add(URI link);

    void remove(URI link);

    void updateLink(URI url, OffsetDateTime updatedAt);

    void setLastCheck(URI checkedLink);

    List<LinkDto> findAll();

    LinkDto findByUrl(URI link);

    List<LinkDto> findByLastCheck(int minutes);

    boolean isExist(URI url);
}
