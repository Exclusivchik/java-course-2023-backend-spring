package edu.java.api.service.linkupdater;

import edu.java.api.domain.dto.LinkDto;

public interface LinkUpdater {
    String getHost();

    int process(LinkDto link);
}
